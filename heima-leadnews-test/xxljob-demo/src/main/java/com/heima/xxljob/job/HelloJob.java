package com.heima.xxljob.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author 12141
 */
@Component
public class HelloJob {


    @Value("${server.port}")
    private int port;

    @XxlJob("demoJobHandler")
    public void helloJob(){
        System.out.println("简单任务执行了~~~"+ port);
    }

}
