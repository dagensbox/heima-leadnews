package com.heima.search.service.impl;

import com.heima.search.pojos.ApUserSearch;
import com.heima.search.service.ApUserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author 12141
 */
@Service
public class ApUserSearchServiceImpl implements ApUserSearchService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    @Async
    public void insert(String keyword, Integer userId) {
        //1、查询当前用户的搜索关键词
        Query query = Query.query(Criteria.where("userId").is(userId).and("keyword").is(keyword));

        ApUserSearch apUserSearch = mongoTemplate.findOne(query, ApUserSearch.class);

        //2、存在 更新创建时间
        if (apUserSearch != null) {
            apUserSearch.setCreatedTime(new Date());
            mongoTemplate.save(apUserSearch);
        }

        //3、不存在，判断当前历史记录总量是否超过10条
        apUserSearch = new ApUserSearch();
        apUserSearch.setUserId(userId);
        apUserSearch.setKeyword(keyword);
        apUserSearch.setCreatedTime(new Date());

        Query query1 = Query.query(Criteria.where("userId").is(userId));
        query1.with(Sort.by(Sort.Direction.DESC, "createTime"));
        List<ApUserSearch> apUserSearches = mongoTemplate.find(query1, ApUserSearch.class);
        if (apUserSearches.size() < 10) {
            mongoTemplate.save(apUserSearch);
        } else {
            ApUserSearch lastUserSearch = apUserSearches.get(apUserSearches.size() - 1);
            mongoTemplate.findAndReplace(Query.query(Criteria.where("id").is(lastUserSearch.getId())), apUserSearch);
        }
    }
}
