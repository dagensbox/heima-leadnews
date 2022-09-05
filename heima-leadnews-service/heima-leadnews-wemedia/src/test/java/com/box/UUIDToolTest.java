package com.box;


import com.alibaba.nacos.common.utils.UuidUtils;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.netflix.util.UUIDFactory;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class UUIDToolTest {


    @Test
    void UUIDTest() {
        System.out.println(UuidUtils.generateUuid());
        System.out.println(UuidUtil.getTimeBasedUuid());
        System.out.println(UUID.randomUUID());
    }
}
