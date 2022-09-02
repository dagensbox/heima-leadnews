package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 12141
 */
public interface ApArticleMapper extends BaseMapper<ApArticle> {
    /**
     * 加载文章列表
     *
     * @param dto  dto
     * @param type 1 加载更多   2 加载最新
     * @return 查询到文章的集合
     */
    List<ApArticle> loadArticleList(@Param("dto") ArticleHomeDto dto, @Param("type") Short type);
}
