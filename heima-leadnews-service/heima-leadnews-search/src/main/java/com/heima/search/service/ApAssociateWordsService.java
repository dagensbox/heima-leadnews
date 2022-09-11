package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dto.UserSearchDto;

/**
 * @author 12141
 */
public interface ApAssociateWordsService {

    /**
     * 联想词语
     * @param dto
     * @return
     */
    ResponseResult findAssociate(UserSearchDto dto);
}
