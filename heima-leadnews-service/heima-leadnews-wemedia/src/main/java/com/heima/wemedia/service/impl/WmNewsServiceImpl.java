package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.WemediaConstants;
import com.heima.common.constants.WmNewsMessageConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.NewsAuthDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.model.wemedia.vos.WmNewsVo;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.heima.wemedia.service.WmNewsService;
import com.heima.wemedia.service.WmNewsTaskService;
import com.heima.wemedia.service.WmUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 12141
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    @Autowired
    private WmUserService wmUserService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private WmNewsTaskService wmNewsTaskService;

    @Override
    public ResponseResult findAll(WmNewsPageReqDto dto) {
        //1.检查参数
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //分页参数检查
        dto.checkParam();
        //获取当前登陆人的信息
        WmUser wmUser = WmThreadLocalUtil.getUser();
        if (wmUser == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        //2.分页条件查询
        IPage<WmNews> page = new Page<>(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmNews> lqw = new LambdaQueryWrapper<>();

        //状态精确查询
        lqw.eq(dto.getStatus() != null, WmNews::getStatus, dto.getStatus());
        //频道精确查询
        lqw.eq(dto.getChannelId() != null, WmNews::getChannelId, dto.getChannelId());
        //时间范围查询
        Date beginPubDate = dto.getBeginPubDate();
        Date endPubDate = dto.getEndPubDate();
        boolean flag = beginPubDate != null && endPubDate != null && beginPubDate.getTime() < endPubDate.getTime();
        lqw.between(flag, WmNews::getPublishTime, beginPubDate, endPubDate);
        //关键字模糊查询
        lqw.like(StringUtils.isNotBlank(dto.getKeyword()), WmNews::getTitle, dto.getKeyword());
        //查询当前登录用户的文章
        lqw.eq(WmNews::getUserId, wmUser.getId());
        //发布时间倒序查询
        lqw.orderByDesc(WmNews::getPublishTime);
        this.page(page, lqw);
        //3.结果返回
        PageResponseResult result = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        result.setData(page.getRecords());
        return result;
    }

    @Override
    public ResponseResult submitNews(WmNewsDto dto) {
        //0.条件判断
        if (dto == null || dto.getContent() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //1.保存或修改文章
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(dto, wmNews, "images");
        // 封面图片list转换为String
        if (dto.getImages() != null && dto.getImages().size() > 0) {
            String imageStr = StringUtils.join(dto.getImages(), ",");
            wmNews.setImages(imageStr);
        }
        saveOrUpdateWmNews(wmNews);

        //2.判断是否为草稿  如果为草稿结束当前方法
        if (dto.getStatus().equals(WmNews.Status.NORMAL.getCode())) {
            return ResponseResult.okResult((AppHttpCodeEnum.SUCCESS));
        }

        //3.不是草稿，保存文章内容图片与素材的关系
        //获取到文章内容中的图片信息
        List<String> materials = extractUrlInfo(dto.getContent());
        //保存文章内容图片与素材的关系
        saveRelativeInfoForContent(materials, wmNews.getId());

        //4.不是草稿，保存文章封面图片与素材的关系，如果当前布局是自动，需要匹配封面图片
        saveRelativeInfoForCover(dto, materials, wmNews);

        //调用自动审核
//        wmNewsAutoScanService.autoScanWmNews(wmNews.getId());
        wmNewsTaskService.addNews2Task(wmNews.getId(), wmNews.getPublishTime());


        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult deleteNewsByNewsId(Integer id) {
        if (id == null) {
            return ResponseResult.errorResult(501, "文章Id不可缺少");
        }
        WmNews wmNews = this.getById(id);
        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        if (wmNews.getStatus() == WmNews.Status.PUBLISHED.getCode() && wmNews.getEnable() == 1) {
            return ResponseResult.errorResult(501, "文章已发布，不能删除");
        }

        wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId, id));
        this.removeById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult downOrUpNews(WmNewsDto dto) {
        //0、参数校验
        if (dto == null || dto.getId() == null || dto.getEnable() == null) {
            return ResponseResult.errorResult(501, "文章Id不可缺少");
        }
        if (dto.getEnable() != 0 && dto.getEnable() != 1) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //1、查找数据
        WmNews wmNews = this.getById(dto.getId());
        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        if (wmNews.getStatus() != WmNews.Status.PUBLISHED.getCode()) {
            return ResponseResult.errorResult(501, "当前文章不是发布状态,不能上下架");
        }
        //3、执行操作
        wmNews.setEnable(dto.getEnable());
        this.updateById(wmNews);
        //发送消息，通知article端修改文章配置
        if (wmNews.getArticleId() != null) {
            Map<String, Object> map = new HashMap<>(4);
            map.put("articleId", wmNews.getArticleId());
            map.put("enabled", dto.getEnable());
            kafkaTemplate.send(WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC, JSON.toJSONString(map));
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult listVo(NewsAuthDto dto) {
        //检查参数
        //检查参数
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        Integer page = dto.getPage();
        Integer size = dto.getSize();
        if (page == null || page <= 0) {
            page = 1;
        }
        if (size == null || size < 0) {
            size = 10;
        }
        //设置查询条件
        IPage<WmNews> iPage = new Page<>(page, size);
        LambdaQueryWrapper<WmNews> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dto.getStatus() != null, WmNews::getStatus, dto.getStatus());
        queryWrapper.like(StringUtils.isNotBlank(dto.getTitle()), WmNews::getTitle, dto.getTitle());
        queryWrapper.orderByDesc(WmNews::getCreatedTime);
        this.page(iPage, queryWrapper);


        PageResponseResult pageResponseResult = new PageResponseResult(page, size, (int) iPage.getTotal());

        List<WmNewsVo> collect = iPage.getRecords().stream().map(item -> {
            WmUser wmUser = wmUserService.getById(item.getUserId());
            WmNewsVo wmNewsVo = new WmNewsVo();
            BeanUtils.copyProperties(item, wmNewsVo);
            wmNewsVo.setAuthorName(wmUser.getName());
            return wmNewsVo;
        }).collect(Collectors.toList());
        pageResponseResult.setData(collect);

        pageResponseResult.setCode(200);
        pageResponseResult.setErrorMessage("操作成功");
        return pageResponseResult;
    }

    /**
     * 第一个功能：如果当前封面类型为自动，则设置封面类型的数据
     * 匹配规则：
     * 1，如果内容图片大于等于1，小于3  单图  type 1
     * 2，如果内容图片大于等于3  多图  type 3
     * 3，如果内容没有图片，无图  type 0
     * 第二个功能：保存封面图片与素材的关系
     *
     * @param dto
     * @param materials
     * @param wmNews
     */
    private void saveRelativeInfoForCover(WmNewsDto dto, List<String> materials, WmNews wmNews) {
        List<String> images = dto.getImages();

        //如果当前封面类型为自动，则设置封面类型的数据
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            //多图
            if (materials.size() >= 3) {
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images = materials.stream().limit(3).collect(Collectors.toList());
            } else if (materials.size() == 1 || materials.size() == 2) {
                //单图
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images = materials.stream().limit(1).collect(Collectors.toList());
            } else {
                //无图
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
                images = new ArrayList<>();
            }
            //修改文章
            if (images.size() > 0) {
                wmNews.setImages(StringUtils.join(images, ","));
            }
            this.updateById(wmNews);
        }
        if (images != null && images.size() > 0) {
            saveRelativeInfo(images, wmNews.getId(), WemediaConstants.WM_COVER_REFERENCE);
        }
    }

    /**
     * 处理文章内容图片与素材的关系
     *
     * @param materials
     * @param newsID
     */
    private void saveRelativeInfoForContent(List<String> materials, Integer newsID) {
        Short type = WemediaConstants.WM_CONTENT_REFERENCE;
        saveRelativeInfo(materials, newsID, type);
    }

    /**
     * 保存文章图片与素材的关系到数据库中
     *
     * @param materials
     * @param newsID
     * @param type
     */
    private void saveRelativeInfo(List<String> materials, Integer newsID, Short type) {
        if (materials == null || materials.isEmpty()) {
            //文章无图片 直接返回
            return;
        }
        //通过图片的url查询素材的id
        List<WmMaterial> dbMaterials = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, materials));

        //判断素材是否有效
        if (dbMaterials == null || dbMaterials.isEmpty()) {
            //手动抛出异常   第一个功能：能够提示调用者素材失效了，第二个功能，进行数据的回滚
            throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
        }
        if (materials.size() != dbMaterials.size()) {
            throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
        }

        List<Integer> idList = dbMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());

        //批量保存
        wmNewsMaterialMapper.saveRelations(idList, newsID, type);
    }


    private List<String> extractUrlInfo(String content) {
        List<String> materials = new ArrayList();

        List<Map> maps = JSONArray.parseArray(content, Map.class);
        for (Map map : maps) {
            if (map.get("type").equals("image")) {
                String imgUrl = (String) map.get("value");
                materials.add(imgUrl);
            }
        }
        return materials;
    }

    /**
     * 保存或修改文章
     *
     * @param wmNews
     */
    private void saveOrUpdateWmNews(WmNews wmNews) {
        //补全属性
        wmNews.setUserId(WmThreadLocalUtil.getUser().getId());
        wmNews.setCreatedTime(new Date());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable((short) 1); //默认上架

        if (wmNews.getId() == null) {
            //保存
            if (wmNews.getType() == -1) {
                wmNews.setType(null);
            }
            this.save(wmNews);
        } else {
            //修改
            //删除文章图片与素材的关系
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId, wmNews.getId()));
            this.updateById(wmNews);
        }
    }
}
