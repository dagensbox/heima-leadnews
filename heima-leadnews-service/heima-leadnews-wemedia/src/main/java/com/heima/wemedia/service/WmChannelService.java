package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;

/**
 * @author 12141
 */
public interface WmChannelService extends IService<WmChannel> {

    /**
     * 查询所有的频道
     *
     * @return
     */
    ResponseResult findAllChannel();
}
