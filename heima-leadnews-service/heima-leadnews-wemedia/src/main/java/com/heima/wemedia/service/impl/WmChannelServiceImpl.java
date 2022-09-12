package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.ChannelDto;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.service.WmChannelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;


/**
 * @author 12141
 */
@Service
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService {
    @Override
    public ResponseResult findAllChannel() {
        return ResponseResult.okResult(this.list());
    }

    @Override
    public ResponseResult delChannelById(Long id) {
        //校验参数
        if (id == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        boolean isDelete = this.removeById(id);
        if (isDelete) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        } else {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
    }

    @Override
    public ResponseResult listByPage(ChannelDto dto) {
        //检查参数
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        Integer page = dto.getPage();
        Integer size = dto.getSize();
        String name = dto.getName();
        if (page == null || page <= 0) {
            page = 1;
        }
        if (size == null || size < 0) {
            size = 10;
        }
        //设置查询条件
        IPage<WmChannel> iPage = new Page<>(page, size);
        this.page(iPage, Wrappers.<WmChannel>lambdaQuery().like(StringUtils.isNotBlank(name), WmChannel::getName, name).orderByAsc(WmChannel::getOrd));

        return ResponseResult.okResult(iPage.getRecords());
    }
}