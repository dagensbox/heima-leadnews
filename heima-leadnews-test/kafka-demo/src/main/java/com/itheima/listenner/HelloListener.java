package com.itheima.listenner;

import com.alibaba.fastjson.JSON;
import com.itheima.pojo.User;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author 12141
 */
@Component
public class HelloListener {


    @KafkaListener(topics = {"it-box"})
    public void onMessage(String message){
        if (StringUtils.isEmpty(message)){
            return;
        }
        User user = JSON.parseObject(message, User.class);
        System.out.println(message);
        System.out.println(user);
    }

}
