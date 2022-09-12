package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.ChannelDto;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.service.WmChannelService;
import com.heima.wemedia.service.WmNewsService;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * @author 12141
 */
@Service
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService {

    @Autowired
    private WmNewsService wmNewsService;

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
        WmChannel byId = this.getById(id);
        if (byId == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        if (byId.getStatus()) {
            return ResponseResult.errorResult(501, "频道处于启用状态，无法删除");
        }
        List<WmNews> list = getRelatedList(byId);

        if (!list.isEmpty()) {
            return ResponseResult.errorResult(501, "频道中存在自媒体文章，无法删除");
        }
        this.removeById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    List<WmNews> getRelatedList(WmChannel wmChannel) {
        List<WmNews> list = wmNewsService.list(Wrappers.<WmNews>lambdaQuery().eq(WmNews::getChannelId, wmChannel.getId()));
        return list;
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

    @Override
    public ResponseResult updateChannel(WmChannel wmChannel) {


        if (wmChannel == null || wmChannel.getId() == null || org.apache.commons.lang.StringUtils.isBlank(wmChannel.getName())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        WmChannel byId = this.getById(wmChannel.getId());
        if (byId == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        if (!byId.getStatus().equals(wmChannel.getStatus()) && !wmChannel.getStatus()) {
            List<WmNews> relatedList = this.getRelatedList(byId);
            if (!relatedList.isEmpty()) {
                return ResponseResult.errorResult(501, "频道中存在自媒体文章，无法禁用");
            }
        }
        wmChannel.setCreatedTime(new Date());
        this.updateById(wmChannel);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}