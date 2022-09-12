package com.heima.wemedia.controller.v1;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.SensitiveDto;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.wemedia.service.WmSensitiveService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author 12141
 */
@RestController
@RequestMapping("/api/v1/sensitive")
public class WmSensitiveController {

    @Autowired
    private WmSensitiveService wmSensitiveService;

    @DeleteMapping("/del/{id}")
    public ResponseResult deleteBySensitiveId(@PathVariable("id") Long id) {
        return wmSensitiveService.deleteBySensitiveId(id);
    }

    @PostMapping("/list")
    public ResponseResult listByPage(@RequestBody SensitiveDto dto) {
        return wmSensitiveService.listByPage(dto);
    }

    @PostMapping("/save")
    public ResponseResult saveSensitive(@RequestBody WmSensitive wmSensitive) {
        if (wmSensitive == null || StringUtils.isBlank(wmSensitive.getSensitives())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        if (wmSensitive.getId() != null) {
            WmSensitive byId = wmSensitiveService.getById(wmSensitive.getId());
            if (byId != null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST);
            }
        }
        WmSensitive one = wmSensitiveService.getOne(Wrappers.<WmSensitive>lambdaQuery().eq(WmSensitive::getSensitives, wmSensitive.getSensitives()));
        if (one != null) {
            return ResponseResult.errorResult(501, "敏感词重复");
        }

        wmSensitive.setCreatedTime(new Date());
        wmSensitiveService.save(wmSensitive);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }


    @PostMapping("/update")
    public ResponseResult updateSensitive(@RequestBody WmSensitive wmSensitive) {
        if (wmSensitive == null || wmSensitive.getId() == null || StringUtils.isBlank(wmSensitive.getSensitives())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        WmSensitive byId = wmSensitiveService.getById(wmSensitive.getId());
        if (byId == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        wmSensitive.setCreatedTime(new Date());
        wmSensitiveService.updateById(wmSensitive);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

}
