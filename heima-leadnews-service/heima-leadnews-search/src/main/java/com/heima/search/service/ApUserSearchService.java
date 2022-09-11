package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dto.HistorySearchDto;

/**
 * @author 12141
 */
public interface ApUserSearchService {

    /**
     * 保存用户搜索记录
     *
     * @param keyword
     * @param userId
     */
    void insert(String keyword, Integer userId);

    /**
     * 查询搜素历史
     */
    ResponseResult findUserSearch();

    /**
     * 根据id删除搜索记录
     *
     * @param dto
     * @return
     */
    ResponseResult delUserSearch(HistorySearchDto dto);
}
