package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.wemedia.IWemediaClient;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.HotArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.vos.HotArticleVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 12141
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class HotArticleServiceImpl implements HotArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private IWemediaClient wemediaClient;


    /**
     * 计算热点问题
     */
    @Override
    public void computeHotArticle() {
        //1、查询前5天的文章数据
        Date date = Date.from(ZonedDateTime.now().minusDays(1500).toInstant());

        List<ApArticle> apArticleList = apArticleMapper.findArticleListByLast5days(date);

        //2、计算文章的分值
        List<HotArticleVo> hotArticleVoList = computeHotArticle(apArticleList);

        //3、为每个频道缓存30条分值较高的文章
        CacheTag2Redis(hotArticleVoList);
    }

    /**
     * 为每个频道缓存30条分值较高的文章
     *
     * @param hotArticleVoList
     */
    private void CacheTag2Redis(List<HotArticleVo> hotArticleVoList) {
        ResponseResult responseResult = wemediaClient.getChannels();
        if (responseResult.getCode().equals(200)) {
            String channelJson = JSON.toJSONString(responseResult.getData());
            List<WmChannel> wmChannels = JSON.parseArray(channelJson, WmChannel.class);
            //检索出每个频道的文章
            if (wmChannels != null && wmChannels.size() > 0) {
                for (WmChannel wmChannel : wmChannels) {
                    List<HotArticleVo> hotArticleVos = hotArticleVoList.stream()
                            .filter(x -> x.getChannelId().equals(wmChannel.getId())).collect(Collectors.toList());
                    sortAndCache(hotArticleVos, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + wmChannel.getId());
                }
            }
        }
        sortAndCache(hotArticleVoList, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + ArticleConstants.DEFAULT_TAG);
    }

    /**
     * 排序并且缓存数据
     *
     * @param hotArticleVos
     * @param key
     */
    private void sortAndCache(List<HotArticleVo> hotArticleVos, String key) {
        hotArticleVos.sort(Comparator.comparing(HotArticleVo::getScore));
        Collections.reverse(hotArticleVos);
        if (hotArticleVos.size() > 30) {
            hotArticleVos = hotArticleVos.subList(0, 30);
        }
        cacheService.set(key, JSON.toJSONString(hotArticleVos));
    }

    /**
     * 计算文章分值
     *
     * @param apArticleList
     * @return
     */
    private List<HotArticleVo> computeHotArticle(List<ApArticle> apArticleList) {
        List<HotArticleVo> hotArticleVoList = new ArrayList<>();
        if (apArticleList == null || apArticleList.isEmpty()) {
            return hotArticleVoList;
        }

        for (ApArticle apArticle : apArticleList) {
            HotArticleVo hotArticleVo = new HotArticleVo();
            BeanUtils.copyProperties(apArticle, hotArticleVo);
            Integer score = computeScore(apArticle);
            hotArticleVo.setScore(score);
            hotArticleVoList.add(hotArticleVo);
        }

        return hotArticleVoList;
    }

    /**
     * 计算单篇文章的分值
     *
     * @param apArticle
     * @return 文章的分值
     */
    private Integer computeScore(ApArticle apArticle) {
        Integer score = 0;
        if (apArticle.getViews() != null) {
            score += apArticle.getViews();
        }
        if (apArticle.getLikes() != null) {
            score += apArticle.getLikes() * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
        }
        if (apArticle.getComment() != null) {
            score += apArticle.getComment() * ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT;
        }
        if (apArticle.getCollection() != null) {
            score += apArticle.getCollection() * ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
        }
        return score;
    }

}
