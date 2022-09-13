package com.heima.apis.wemedia;

import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author 12141
 */
@FeignClient("leadnews-wemedia")
public interface IWemediaClient {

    /**
     * 获取频道列表的远程接口
     *
     * @return
     */
    @GetMapping("/api/v1/channel/list")
    public ResponseResult getChannels();


}
