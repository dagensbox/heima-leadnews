package com.heima.wemedia.service;

import java.util.Date;

/**
 * @author 12141
 */
public interface WmNewsTaskService {

    /**
     * 添加新闻任务到延迟队列
     *
     * @param id 文章id
     * @param PublishTime 发布时间  可以作为任务的执行时间
     */
    void addNews2Task(Integer id, Date PublishTime);

    /**
     * 消费延迟队列
     */
    void scanNewsByTask();
}
