package com.heima.wemedia.service;

import com.heima.model.wemedia.pojos.WmNews;

/**
 * @author 12141
 */
public interface WmNewsAutoScanService {

    /**
     * 自媒体文章审核
     *
     * @param id 自媒体文章id
     */
    void  autoScanWmNews(Integer id);

    /**
     * 审核完成后调用，用来 使用feign远程调用文章微服务生成文章，并回填wmnewsd
     *
     * @param wmNews
     */
    void saveApArticleAndUpdateWmNews(WmNews wmNews);
}
