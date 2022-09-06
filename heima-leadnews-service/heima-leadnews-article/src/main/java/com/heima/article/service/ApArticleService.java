package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;

/**
 * @author 12141
 */
public interface ApArticleService extends IService<ApArticle> {

    /**
     * 根据参数加载文章列表
     *
     * @param loadType 1为加载更多 2为加载最新
     * @param dto
     * @return 结果返回
     */
    ResponseResult load(Short loadType, ArticleHomeDto dto);

    /**
     * 保存app端文章
     *
     * @param dto
     * @return
     */
    ResponseResult saveArticle(ArticleDto dto);
}
