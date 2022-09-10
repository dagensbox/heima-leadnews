package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.apis.article.IArticleClient;
import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.common.tess4j.Tess4jClient;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.SensitiveWordUtil;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 12141
 */
@Service
@Slf4j
@Transactional(rollbackFor = {Exception.class})
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {

    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Autowired
    private WmChannelMapper wmChannelMapper;

    @Autowired
    private WmUserMapper wmUserMapper;

    @Autowired
    private WmSensitiveMapper wmSensitiveMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private GreenTextScan greenTextScan;

    @Autowired
    private GreenImageScan greenImageScan;

    @Autowired
    private IArticleClient articleClient;

    @Autowired
    private Tess4jClient tess4jClient;

    @Override
    @Async
    public void autoScanWmNews(Integer id) {
        //0、参数校验
        if (id == null) {
            throw new RuntimeException("WeNewsAutoScanServiceImpl-传入id参数为空");
        }

        //1、查询自媒体文章
        WmNews wmNews = wmNewsMapper.selectById(id);
        if (wmNews == null) {
            throw new RuntimeException("WeNewsAutoScanServiceImpl-文章不存在");
        }
        if (wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {
            //从内容中提取纯文本内容和图片
            Map<String, Object> textAndImages = handleTextAndImages(wmNews);

            //自管理的敏感词过滤
            boolean isSensitive = handleSensitiveScan((String) textAndImages.get("content"), wmNews);
            if (!isSensitive) {
                return;
            }

            //2、审核文本内容  阿里云接口
            boolean isTextScan = handleTextScan((String) textAndImages.get("content"), wmNews);
            if (!isTextScan) {
                return;
            }
            //3、审核图片  阿里云接口
            boolean isImageScan = handleImageScan((List<String>) textAndImages.get("images"), wmNews);
            if (!isImageScan) {
                return;
            }
            //4、审核成功，保存app端的相关文章数据
            ResponseResult result = saveAppArticle(wmNews);
            if (!result.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
                throw new RuntimeException("WmNewsAutoScanServiceImpl-文章审核，保存app端相关文章数据失败");
            }
            //回填article_id
            wmNews.setArticleId((Long) result.getData());
            updateWmNews(wmNews, WmNews.Status.PUBLISHED.getCode(), "审核成功");
        }

    }

    /**
     * 自管理的敏感词审核
     *
     * @param content
     * @return
     */
    private boolean handleSensitiveScan(String content, WmNews wmNews) {
        boolean flag = true;

        //获取所有的敏感词
        List<WmSensitive> wmSensitives = wmSensitiveMapper.selectList(Wrappers.<WmSensitive>lambdaQuery()
                .select(WmSensitive::getSensitives));
        List<String> sensitiveList = wmSensitives.stream().map(WmSensitive::getSensitives).collect(Collectors.toList());

        //初始化敏感词库
        SensitiveWordUtil.initMap(sensitiveList);

        //查看文章中是否包含敏感词
        Map<String, Integer> matchWords = SensitiveWordUtil.matchWords(content + wmNews.getTitle());
        if (matchWords.size() > 0){
            this.updateWmNews(wmNews,WmNews.Status.FAIL.getCode(),"当前文章中存在违规内容"+matchWords);
            flag = false;
        }
        return flag;
    }

    /**
     * 保存app端相关的文章数据
     *
     * @param wmNews
     * @return
     */
    private ResponseResult saveAppArticle(WmNews wmNews) {
        ArticleDto dto = new ArticleDto();
        //属性的拷贝
        BeanUtils.copyProperties(wmNews, dto, "id");
        dto.setId(wmNews.getArticleId());
        //文章的布局
        dto.setLayout(wmNews.getType());
        //频道
        WmChannel wmChannel = wmChannelMapper.selectById(wmNews.getChannelId());
        if (wmChannel != null) {
            dto.setChannelName(wmChannel.getName());
        }

        //作者
        dto.setAuthorId(wmNews.getUserId().longValue());
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if (wmUser != null) {
            dto.setAuthorName(wmUser.getName());
        }

        //设置文章id
        if (wmNews.getArticleId() != null) {
            dto.setId(wmNews.getArticleId());
        }
        dto.setCreatedTime(new Date());

        ResponseResult result = articleClient.saveArticle(dto);

        return result;
    }

    /**
     * 审核图片内容
     *
     * @param images
     * @param wmNews
     * @return
     */
    private boolean handleImageScan(List<String> images, WmNews wmNews) {
        boolean flag = true;

        if (images == null || images.isEmpty()) {
            return flag;
        }

        //从Minio下载图片
        //图片去重
        List<byte[]> imageList = images.stream().distinct().map(image -> fileStorageService.downLoadFile(image))
                .collect(Collectors.toList());


        //图片识别文字审核---begin-----

        try {
            //从byte[]转换为butteredImage
            for (byte[] bytes : imageList) {

                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                BufferedImage imageFile = ImageIO.read(in);
                //识别图片中的文字
                String result = tess4jClient.doOCR(imageFile);

                //审核是否包含自己管理的敏感词
                boolean isSensitive = handleSensitiveScan(result, wmNews);
                if (!isSensitive){
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //审核图片
        try {
            Map map = greenImageScan.imageScan(imageList);
            if (map != null) {
                //审核失败
                if ("block".equals(map.get("suggestion"))) {
                    flag = false;
                    updateWmNews(wmNews, WmNews.Status.FAIL.getCode(), "当前图片中存在违规内容");
                }
                if ("review".equals(map.get("suggestion"))) {
                    flag = false;
                    updateWmNews(wmNews, WmNews.Status.ADMIN_AUTH.getCode(), "当前图片中存在不确定内容");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /**
     * 审核纯文本内容
     *
     * @param content
     * @param wmNews
     * @return
     */
    private boolean handleTextScan(String content, WmNews wmNews) {
        boolean flag = true;
        content = content + "-" + wmNews.getTitle();
        if (content.length() == 1) {
            return true;
        }

        try {
            Map map = greenTextScan.greeTextScan(content);
            if (map != null) {
                //审核失败
                if ("block".equals(map.get("suggestion"))) {
                    flag = false;
                    updateWmNews(wmNews, WmNews.Status.FAIL.getCode(), "当前文章中存在违规内容");
                }
                if ("review".equals(map.get("suggestion"))) {
                    flag = false;
                    updateWmNews(wmNews, WmNews.Status.ADMIN_AUTH.getCode(), "当前文章中存在不确定内容");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /**
     * 修改文章审核信息
     *
     * @param wmNews
     * @param status
     * @param reason
     */
    private void updateWmNews(WmNews wmNews, short status, String reason) {
        wmNews.setStatus(status);
        wmNews.setReason(reason);
        wmNewsMapper.updateById(wmNews);
    }


    /**
     * 1、从自媒体文章的内容中提取文本和图片
     * 2、提取文章的封面图片
     *
     * @param wmNews
     * @return
     */
    @NotNull
    private static Map<String, Object> handleTextAndImages(WmNews wmNews) {
        Map<String, Object> textAndImages = new HashMap<>(8);
        //使用stringBuilder 储存纯文本内容
        StringBuilder sb = new StringBuilder();
        //使用 list 储存图片内容
        List<String> images = new ArrayList<>();
        //1、从自媒体文章的内容中提取文本和图片
        String content = wmNews.getContent();
        if (StringUtils.isNotBlank(content)) {
            List<Map> mapList = JSONArray.parseArray(content, Map.class);
            for (Map map : mapList) {
                if ("text".equals(map.get("type"))) {
                    sb.append(map.get("value"));
                }
                if ("image".equals(map.get("type"))) {
                    images.add((String) map.get("value"));
                }
            }
        }
        //2、提取文章的封面图片
        if (StringUtils.isNotBlank(wmNews.getImages())) {
            String[] split = wmNews.getImages().split(",");
            images.addAll(Arrays.asList(split));
        }
        textAndImages.put("content", sb.toString());
        textAndImages.put("images", images);
        return textAndImages;
    }
}
