package com.heima.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author 12141
 */
@Service
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    private static final Integer MAX_PAGE_SIZE = 50;
    private static final Integer DEFAULT_PAGE_SIZE = 10;

    @Override
    public ResponseResult load(Short loadType, ArticleHomeDto dto) {
        //1、校验参数

        //分页size校验
        Integer size = dto.getSize();
        if (size == null || size <= 0) {
            size = DEFAULT_PAGE_SIZE;
        }
        size = Math.max(size, MAX_PAGE_SIZE);
        dto.setSize(size);

        //类型参数校验
        if (loadType == null || (!loadType.equals(ArticleConstants.LOADTYPE_LOADMORE) && !loadType.equals(ArticleConstants.LOADTYPE_LOADNEW))) {
            loadType = ArticleConstants.LOADTYPE_LOADMORE;
        }

        //文章频道校验
        if (StringUtils.isBlank(dto.getTag())) {
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        //时间校验
        if (dto.getMaxBehotTime() == null) {
            dto.setMaxBehotTime(new Date());
        }
        if (dto.getMinBehotTime() == null) {
            dto.setMinBehotTime(new Date(0));
        }

        //2、查询数据
        List<ApArticle> apArticleList = apArticleMapper.loadArticleList(dto, loadType);
        //3、结果封装
        return ResponseResult.okResult(apArticleList);
    }
}
