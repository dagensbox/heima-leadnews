package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author 12141
 */
public interface WmMaterialService extends IService<WmMaterial> {
    /**
     * 图片上传
     *
     * @param multipartFile
     * @return
     */
    ResponseResult uploadPicture(MultipartFile multipartFile);

    /**
     * 图片素材管理查询
     *
     * @param dto
     * @return
     */
    ResponseResult findList(WmMaterialDto dto);

    /**
     * 根据图片id删除图片 若该图片正在被某文章用到则不能删除
     *
     * @param id
     * @return
     */
    ResponseResult deletePicByPicId(Integer id);


    /**
     * 收藏或取消收藏接口
     *
     * @param id
     * @param isCollection 0取消收藏 1收藏
     * @return
     */
    ResponseResult collectOperationByPicId(Integer id, Short isCollection);
}
