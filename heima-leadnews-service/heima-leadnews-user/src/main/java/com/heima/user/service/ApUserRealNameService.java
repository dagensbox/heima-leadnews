package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.ApUserAuthDto;
import com.heima.model.user.pojos.ApUserRealname;

/**
 * @author 12141
 */
public interface ApUserRealNameService extends IService<ApUserRealname> {

    /**
     * 查询列表
     */
    ResponseResult listByPage(ApUserAuthDto dto);

    /**
     * 审核失败
     *
     * @param dto
     * @return
     */
    ResponseResult authFail(ApUserAuthDto dto);

    /**
     * 审核成功
     */
    ResponseResult authPass(ApUserAuthDto dto);
}
