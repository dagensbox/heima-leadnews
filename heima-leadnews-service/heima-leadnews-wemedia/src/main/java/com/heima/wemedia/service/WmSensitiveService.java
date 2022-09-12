package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.SensitiveDto;
import com.heima.model.wemedia.pojos.WmSensitive;

/**
 * @author 12141
 */
public interface WmSensitiveService extends IService<WmSensitive> {

    /**
     * 根据敏感词id删除
     * @param id
     * @return
     */
    ResponseResult deleteBySensitiveId(Long id);

    /**
     * 分页查询
     *
     * @param dto
     * @return
     */
    ResponseResult listByPage(SensitiveDto dto);
}
