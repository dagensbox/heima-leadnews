package com.heima.wemedia;

import com.heima.wemedia.service.WmNewsAutoScanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {WemediaApplication.class})
public class WmNewsAutoScanServiceTest {

    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    @Test
    public void testAutoScan() {
        wmNewsAutoScanService.autoScanWmNews(6244);
    }


}
