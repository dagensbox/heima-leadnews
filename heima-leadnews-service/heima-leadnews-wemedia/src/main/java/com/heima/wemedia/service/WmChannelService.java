package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.ChannelDto;
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

    /**
     * 根据频道id删除
     *
     * @param id
     * @return
     */
    ResponseResult delChannelById(Long id);

    /**
     * 分页模糊查询
     *
     * @param dto
     * @return
     */
    ResponseResult listByPage(ChannelDto dto);

    /**
     * 更新频道
     */
    ResponseResult updateChannel(WmChannel wmChannel);
}
