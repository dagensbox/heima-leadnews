package com.heima.schedule.service;

import com.heima.model.schedule.dtos.Task;

/**
 * @author 12141
 */
public interface TaskService {
    /**
     * 添加延迟任务
     *
     * @param task 任务对象
     * @return 任务id
     */
    public long addTask(Task task);
}
