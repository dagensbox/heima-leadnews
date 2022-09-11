package com.heima.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import com.heima.es.mapper.ApArticleMapper;
import com.heima.es.pojo.SearchArticleVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ApArticleTest {


    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ElasticsearchClient client;

    /**
     * 注意：数据量的导入，如果数据量过大，需要分页导入
     * @throws Exception
     */
    @Test
    public void init() throws Exception {
        //数据很小 不需要分页
        List<SearchArticleVo> vos = apArticleMapper.loadArticleList();

        List<BulkOperation> bulkOperations = vos.stream().map(vo ->
                new BulkOperation.Builder().create(builder -> builder.id(vo.getId().toString()).document(vo)).build()
        ).collect(Collectors.toList());

        BulkResponse bulkResponse = client.bulk(req -> req.index("app_info_article").operations(bulkOperations));
        System.out.println(bulkResponse);
    }

}