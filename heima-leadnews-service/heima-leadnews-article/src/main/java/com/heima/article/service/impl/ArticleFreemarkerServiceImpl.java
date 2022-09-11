package com.heima.article.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.constants.ArticleConstants;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.search.vos.SearchArticleVo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 12141
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ArticleFreemarkerServiceImpl implements ArticleFreemarkerService {


    @Autowired
    private ApArticleMapper apArticleMapper;


    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    @Override
    @Async
    public void buildArticle2Minio(ApArticle apArticle, String content) {
        //文章id已知
        try {
            //1、获取文章内容
            if (StringUtils.isNotBlank(content)) {
                //2、文章内容通过freemarker生成html
                StringWriter out = new StringWriter();
                Template template = configuration.getTemplate("article.ftl");

                Map<String, Object> params = new HashMap<>(4);
                params.put("content", JSONArray.parseArray(content));

                template.process(params, out);

                InputStream is = new ByteArrayInputStream(out.toString().getBytes());

                //3、把html上传到minio中
                String path = fileStorageService.uploadHtmlFile("", apArticle.getId() + ".html", is);

                //4、修改ap_article表,保存static_url字段
                apArticle.setStaticUrl(path);
                apArticleMapper.updateById(apArticle);

                //发送消息，建立索引
                createArticleEsIndex(apArticle, content, path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息建立索引
     */
    private void createArticleEsIndex(ApArticle apArticle, String content, String path) {
        SearchArticleVo vo = new SearchArticleVo();
        BeanUtils.copyProperties(apArticle, vo);
        vo.setContent(content);
        vo.setStaticUrl(path);
        kafkaTemplate.send(ArticleConstants.ARTICLE_ES_SYNC_TOPIC, JSON.toJSONString(vo));
    }
}

