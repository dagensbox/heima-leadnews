package com.heima.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.UserConstants;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.ApUserAuthDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.pojos.ApUserRealname;
import com.heima.user.mapper.ApUserRealNameMapper;
import com.heima.user.service.ApUserRealNameService;
import com.heima.user.service.ApUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author 12141
 */
@Service
public class ApUserRealNameServiceImpl extends ServiceImpl<ApUserRealNameMapper, ApUserRealname> implements ApUserRealNameService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ApUserService apUserService;

    @Override
    public ResponseResult listByPage(ApUserAuthDto dto) {
        //检查参数
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        Integer page = dto.getPage();
        Integer size = dto.getSize();
        Integer status = dto.getStatus();
        if (page == null || page <= 0) {
            page = 1;
        }
        if (size == null || size < 0) {
            size = 10;
        }
        //设置查询条件
        IPage<ApUserRealname> iPage = new Page<>(page, size);
        this.page(iPage, Wrappers.<ApUserRealname>lambdaQuery().like(status != null, ApUserRealname::getStatus, status).orderByDesc(ApUserRealname::getUpdatedTime));

        return ResponseResult.okResult(iPage.getRecords());
    }


    @Override
    public ResponseResult authFail(ApUserAuthDto dto) {
        //检查参数
        if (dto == null || dto.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApUserRealname apUserRealname = this.getById(dto.getId());
        if (apUserRealname == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        apUserRealname.setStatus((short) 2);
        apUserRealname.setReason(dto.getMsg());
        apUserRealname.setUpdatedTime(new Date());
        this.updateById(apUserRealname);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult authPass(ApUserAuthDto dto) {
        //检查参数
        if (dto == null || dto.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApUserRealname apUserRealname = this.getById(dto.getId());
        apUserRealname.setStatus((short) 9);
        apUserRealname.setUpdatedTime(new Date());
        this.updateById(apUserRealname);
        //发送消息到自媒体微服务以创建账号
        ApUser one = apUserService.getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getId, apUserRealname.getUserId()));
        kafkaTemplate.send(UserConstants.USER_CHANGE_TOPIC, JSON.toJSONString(one));
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
