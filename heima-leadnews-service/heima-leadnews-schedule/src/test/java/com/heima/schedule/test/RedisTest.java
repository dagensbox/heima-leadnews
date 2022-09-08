package com.heima.schedule.test;

import com.heima.common.redis.CacheService;
import com.heima.schedule.ScheduleApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest(classes = {ScheduleApplication.class})
public class RedisTest {

    @Autowired
    private CacheService cacheService;

    @Test
    void testList() {
        //在list的左边添加元素
        //cacheService.lLeftPush("list_001", "hello,box! from redis");

        //在list的右边获取元素，并删除 pop 拉屎
        String list_001 = cacheService.lRightPop("list_001");
        System.out.println(list_001);
    }


    @Test
    void testZSet() {
        //添加数据到zset中 分值
        /*cacheService.zAdd("zset_key_001","hello zset 001",1000);
        cacheService.zAdd("zset_key_001","hello zset 002",8888);
        cacheService.zAdd("zset_key_001","hello zset 003",9999);
        cacheService.zAdd("zset_key_001","hello zset 004",4444);
        cacheService.zAdd("zset_key_001","hello zset 005",178985);*/

        //按照分值获取数据
        Set<String> zset_key_001 = cacheService.zRangeByScore("zset_key_001", 0, 8889);
        System.out.println(zset_key_001);
    }
}
