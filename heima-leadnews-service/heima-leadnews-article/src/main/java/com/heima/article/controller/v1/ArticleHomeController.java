package com.heima.article.controller.v1;

import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 12141
 */
@RestController
@RequestMapping("/api/v1/article")
@Api(value = "app端首页显示", tags = "ap_article")
public class ArticleHomeController {

    @Autowired
    private ApArticleService apArticleService;

    @PostMapping("/load")
    @ApiOperation("加载首页")
    public ResponseResult load(@RequestBody ArticleHomeDto dto) {
//        return apArticleService.load(ArticleConstants.LOADTYPE_LOADMORE, dto);
        return apArticleService.load2(ArticleConstants.LOADTYPE_LOADMORE, dto, true);
    }

    @PostMapping("/loadmore")
    @ApiOperation("加载更多")
    public ResponseResult loadmore(@RequestBody ArticleHomeDto dto) {
        return apArticleService.load(ArticleConstants.LOADTYPE_LOADMORE, dto);
    }

    @PostMapping("/loadnew")
    @ApiOperation("加载最新")
    public ResponseResult loadnew(@RequestBody ArticleHomeDto dto) {
        return apArticleService.load(ArticleConstants.LOADTYPE_LOADNEW, dto);
    }


    @PostMapping("/save")
    ResponseResult saveArticle(@RequestBody ArticleDto dto) {
        return apArticleService.saveArticle(dto);
    }
}
