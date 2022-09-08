package com.heima.schedule.service;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.ScheduleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.schedule.pojos.TaskinfoLogs;
import com.heima.schedule.mapper.TaskinfoLogsMapper;
import com.heima.schedule.mapper.TaskinfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * @author 12141
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private TaskinfoMapper taskinfoMapper;

    @Autowired
    private TaskinfoLogsMapper taskinfoLogsMapper;


    @Override
    public long addTask(Task task) {
        //1、添加任务到数据库中
        boolean success = addTask2Db(task);

        if (success) {
            //2、添加任务到redis中
            addTask2Cache(task);
        }

        return task.getTaskId();
    }

    @Override
    public boolean cancelTask(long taskId) {
        boolean flag = false;

        //1、删除任务，更新日志
        Task task = updateDb(taskId, ScheduleConstants.EXECUTED);

        //2、删除redis中的数据
        if (task != null) {
            removeTaskFromCache(task);
            flag = true;
        }

        return flag;
    }

    /**
     * 删除redis中的任务数据
     *
     * @param task
     */
    private void removeTaskFromCache(Task task) {
        String key = task.getTaskType() + "_" + task.getPriority();

        if (task.getExecuteTime() <= System.currentTimeMillis()) {
            cacheService.lRemove(ScheduleConstants.TOPIC + key, 0, JSON.toJSONString(task));
        } else {
            cacheService.zRemove(ScheduleConstants.FUTURE + key, JSON.toJSONString(task));
        }

    }

    /**
     * 删除任务，更新任务日志状态
     *
     * @param taskId
     * @param status
     * @return
     */
    private Task updateDb(long taskId, int status) {
        Task task = null;

        try {
            //删除任务
            taskinfoMapper.deleteById(taskId);

            TaskinfoLogs taskinfoLogs = taskinfoLogsMapper.selectById(taskId);
            taskinfoLogs.setStatus(status);
            taskinfoLogsMapper.updateById(taskinfoLogs);

            task = new Task();
            BeanUtils.copyProperties(taskinfoLogs, task);
            task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());
        } catch (BeansException e) {
            log.error("task cancel exception taskId={}", taskId);
        }

        return task;
    }

    /**
     * 将任务添加到redis中
     *
     * @param task
     */
    private void addTask2Cache(Task task) {
        String key = task.getTaskType() + "_" + task.getPriority();

        //获取5分钟之后的时间
        LocalDateTime after5 = LocalDateTime.now().plusMinutes(5);
        long nextScheduleTime = after5.toInstant(ZoneOffset.of("+8")).toEpochMilli();

        //2.1 如果任务的执行时间小于等于当前时间，存入list
        if (task.getExecuteTime() <= System.currentTimeMillis()) {
            cacheService.lLeftPush(ScheduleConstants.TOPIC + key, JSON.toJSONString(task));
        } else if (task.getExecuteTime() <= nextScheduleTime) {
            //2.2 如果任务的执行时间大于当前时间 && 小于等于预设时间（未来5分钟） 存入zset中
            cacheService.zAdd(ScheduleConstants.FUTURE + key, JSON.toJSONString(task), task.getExecuteTime());
        }


    }

    /**
     * 将任务添加到mysql数据库
     *
     * @param task
     * @return
     */
    private boolean addTask2Db(Task task) {
        boolean flag = false;

        try {
            //保存任务表
            Taskinfo taskinfo = new Taskinfo();
            BeanUtils.copyProperties(task, taskinfo);
            taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
            taskinfoMapper.insert(taskinfo);

            //mp插入后，taskId自动回填 插入task
            task.setTaskId(taskinfo.getTaskId());

            //保存任务日志数据
            TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
            BeanUtils.copyProperties(taskinfo, taskinfoLogs);
            taskinfoLogs.setVersion(1);
            taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
            taskinfoLogsMapper.insert(taskinfoLogs);

            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }


}
