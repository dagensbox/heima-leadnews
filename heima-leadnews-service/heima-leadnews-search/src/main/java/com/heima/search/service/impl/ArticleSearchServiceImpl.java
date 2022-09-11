package com.heima.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.dto.UserSearchDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.search.pojos.ApUserSearch;
import com.heima.search.service.ApUserSearchService;
import com.heima.search.service.ArticleSearchService;
import com.heima.utils.thread.AppThreadLocalUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ArticleSearchServiceImpl implements ArticleSearchService {

    @Autowired
    private ElasticsearchClient esClient;


    @Autowired
    private ApUserSearchService apUserSearchService;

    @Override
    public ResponseResult search(UserSearchDto dto) throws IOException {
        //1、检查参数
        if (dto == null || StringUtils.isBlank(dto.getSearchWords())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //异步调用 保存搜索记录
        ApUser user = AppThreadLocalUtil.getUser();
        if (user != null && dto.getFromIndex() == 0) {
            apUserSearchService.insert(dto.getSearchWords(), user.getId());
        }


        //2、设置查询条件
        List<Query> queries = new ArrayList<>();
        //匹配标题和内容
        Query query1 = Query.of(builder -> builder.multiMatch(builder1 -> builder1.fields("title", "content").query(dto.getSearchWords())));
        //时间条件
        Query query2 = Query.of(builder -> builder.range(builder1 -> builder1.field("publishTime").lte(JsonData.of(dto.getMinBehotTime().getTime()))));
        queries.add(query1);
        queries.add(query2);

        BoolQuery boolQuery = BoolQuery.of(builder -> builder.must(queries));

        //设置高亮 title
        Highlight highlight = Highlight.of(builder -> builder.fields("title", builder1 -> builder1
                .matchedFields(dto.getSearchWords())
                .preTags("<font style='color: red; font-size: inherit;'>")
                .postTags("</font>")
                .requireFieldMatch(false)));

        SearchResponse<Map> searchResponse = esClient.search(builder -> builder.index("app_info_article")
                        .query(builder1 -> builder1.bool(boolQuery))
                        .highlight(highlight)
                        .sort(builder1 -> builder1.field(builder2 -> builder2.field("publishTime").order(SortOrder.Desc)))
                        .from(0)
                        .size(dto.getPageSize())
                , Map.class);

        //3、封装结果返回
        List<Map> list = new ArrayList<>();

        List<Hit<Map>> hits = searchResponse.hits().hits();

        for (Hit<Map> hit : hits) {
            Map source = hit.source();
            //处理高亮
            if (hit.highlight() != null && hit.highlight().size() > 0) {
                List<String> titles = hit.highlight().get("title");
                String title = StringUtils.join(titles);
                source.put("h_title", title);
            } else {
                source.put("h_title", source.get("title"));
            }
            list.add(source);
        }
        return ResponseResult.okResult(list);
    }
}
