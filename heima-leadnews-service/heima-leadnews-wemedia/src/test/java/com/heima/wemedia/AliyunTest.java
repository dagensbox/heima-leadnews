package com.heima.wemedia;

import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.file.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Map;

@SpringBootTest(classes = {WemediaApplication.class})
public class AliyunTest {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private GreenImageScan greenImageScan;

    @Autowired
    private GreenTextScan greenTextScan;;

    @Test
    void testScanText() throws Exception {
        Map map = greenTextScan.greeTextScan("我爱冰毒！");
        System.out.println(map);
    }


    @Test
    void testScanImage() throws Exception {
        byte[] bytes = fileStorageService.downLoadFile("http://192.168.80.1:9000/leadnews/yellow/17489f4bc15285a0.gif");
        Map map = greenImageScan.imageScan(Arrays.asList(bytes));
        System.out.println(map);
    }

}