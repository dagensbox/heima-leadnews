package com.heima.article.service;

import com.heima.model.article.pojos.ApArticle;

/**
 * @author 12141
 */
public interface ArticleFreemarkerService {


    /**
     * 生成静态文件上传到minIO中
     * @param article
     * @param content
     */
    void buildArticle2Minio(ApArticle article,String content);
}
