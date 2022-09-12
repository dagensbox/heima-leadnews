package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.NewsAuthDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;

/**
 * @author 12141
 */
public interface WmNewsService extends IService<WmNews> {

    /**
     * 根据dto条件查询
     *
     * @param dto
     * @return
     */
    ResponseResult findAll(WmNewsPageReqDto dto);

    /**
     * 发布文章或保存草稿
     *
     * @param dto
     * @return
     */
    ResponseResult submitNews(WmNewsDto dto);


    /**
     * 根据文章id删除文章 及关联的关联表
     *
     * @param id
     * @return
     */
    ResponseResult deleteNewsByNewsId(Integer id);

    /**
     * 文章上下架
     *
     * @param dto
     * @return
     */
    ResponseResult downOrUpNews(WmNewsDto dto);

    /**
     * 查询文章列表
     *
     * @param dto
     * @return
     */
    ResponseResult listVo(NewsAuthDto dto);
}
