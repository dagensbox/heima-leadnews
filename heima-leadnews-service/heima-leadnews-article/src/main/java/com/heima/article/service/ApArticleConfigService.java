package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.pojos.ApArticleConfig;

import java.util.Map;

/**
 * @author 12141
 */
public interface ApArticleConfigService extends IService<ApArticleConfig> {
    void updateByMap(Map map);

}
