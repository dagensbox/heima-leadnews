package com.heima.wemedia.listener;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.UserConstants;
import com.heima.model.user.pojos.ApUser;
import com.heima.wemedia.service.WmUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author 12141
 */
@Component
@Slf4j
public class CreateWmUserListener {

    @Autowired
    private WmUserService wmUserService;

    @KafkaListener(topics = {UserConstants.USER_CHANGE_TOPIC})
    private void onMessage(String msg) {
        ApUser apUser = JSON.parseObject(msg, ApUser.class);
        wmUserService.createWmUserByApUser(apUser);
    }

}
