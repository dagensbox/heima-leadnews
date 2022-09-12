package com.heima.wemedia.controller.v1;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.ChannelDto;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.wemedia.service.WmChannelService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author 12141
 */
@RestController
@RequestMapping("/api/v1/channel")
public class WmChannelController {

    @Autowired
    private WmChannelService wmChannelService;

    @GetMapping("/channels")
    public ResponseResult findAllChannel() {
        return wmChannelService.findAllChannel();
    }

    @GetMapping("/del/{id}")
    public ResponseResult delChannelById(@PathVariable Long id) {
        return wmChannelService.delChannelById(id);
    }

    @PostMapping("/list")
    public ResponseResult listByPage(@RequestBody ChannelDto dto) {
        return wmChannelService.listByPage(dto);
    }

    @PostMapping("/save")
    public ResponseResult saveChannel(@RequestBody WmChannel wmChannel) {
        if (wmChannel == null || StringUtils.isBlank(wmChannel.getName())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        if (wmChannel.getId() != null) {
            WmChannel byId = wmChannelService.getById(wmChannel.getId());
            if (byId != null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST);
            }
        }
        WmChannel one = wmChannelService.getOne(Wrappers.<WmChannel>lambdaQuery().eq(WmChannel::getName, wmChannel.getName()));
        if (one != null) {
            return ResponseResult.errorResult(501, "频道名字重复");
        }

        wmChannel.setCreatedTime(new Date());
        wmChannelService.save(wmChannel);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @PostMapping("/update")
    public ResponseResult updateChannel(@RequestBody WmChannel wmChannel) {
        return wmChannelService.updateChannel(wmChannel);
    }

}
