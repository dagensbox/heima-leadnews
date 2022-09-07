package com.heima.article.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author 12141
 */
@Service
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private ArticleFreemarkerService articleFreemarkerService;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    private static final Integer MAX_PAGE_SIZE = 50;
    private static final Integer DEFAULT_PAGE_SIZE = 10;

    @Override
    public ResponseResult load(Short loadType, ArticleHomeDto dto) {
        //1、校验参数

        //分页size校验
        Integer size = dto.getSize();
        if (size == null || size <= 0) {
            size = DEFAULT_PAGE_SIZE;
        }
        size = Math.max(size, MAX_PAGE_SIZE);
        dto.setSize(size);

        //类型参数校验
        if (loadType == null || (!loadType.equals(ArticleConstants.LOADTYPE_LOADMORE) && !loadType.equals(ArticleConstants.LOADTYPE_LOADNEW))) {
            loadType = ArticleConstants.LOADTYPE_LOADMORE;
        }

        //文章频道校验
        if (StringUtils.isBlank(dto.getTag())) {
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        //时间校验
        if (dto.getMaxBehotTime() == null) {
            dto.setMaxBehotTime(new Date());
        }
        if (dto.getMinBehotTime() == null) {
            dto.setMinBehotTime(new Date(0));
        }

        //2、查询数据
        List<ApArticle> apArticleList = apArticleMapper.loadArticleList(dto, loadType);
        //3、结果封装
        return ResponseResult.okResult(apArticleList);
    }

    @Override
    public ResponseResult saveArticle(ArticleDto dto) {
        //1、检查参数
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(dto, apArticle);

        //2、判断是否存在id
        if (dto.getId() == null) {
            //2.1 不存在id  保存  文章  文章配置  文章内容

            //保存文章
            this.save(apArticle);
            //保存文章配置
            apArticleConfigMapper.insert(new ApArticleConfig(apArticle.getId()));
            //保存文章
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.insert(apArticleContent);
        } else {
            ApArticle byId = this.getById(apArticle.getId());
            if (byId == null){
                return ResponseResult.errorResult(501,"文章没有找到");
            }
            //修改文章
            updateById(apArticle);
            //保存文章内容
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery()
                    .eq(ApArticleContent::getArticleId, apArticle.getId()));
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.updateById(apArticleContent);
        }

        //异步调用 生成静态文件上传到minio中
        articleFreemarkerService.buildArticle2Minio(apArticle,dto.getContent());

        return ResponseResult.okResult(apArticle.getId());
    }
}
