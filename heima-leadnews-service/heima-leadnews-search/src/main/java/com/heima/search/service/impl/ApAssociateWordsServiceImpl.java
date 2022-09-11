package com.heima.search.service.impl;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.dto.UserSearchDto;
import com.heima.search.pojos.ApAssociateWords;
import com.heima.search.service.ApAssociateWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * @author 12141
 */
@Service
public class ApAssociateWordsServiceImpl implements ApAssociateWordsService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseResult findAssociate(UserSearchDto dto) {
        //参数检查
        if (dto == null || dto.getSearchWords() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //分页检查
        if (dto.getPageSize() > 20) {
            dto.setPageSize(20);
        }

        //执行查询 模糊查询
        Query query = Query.query(Criteria.where("associateWords").regex(".*?\\" + dto.getSearchWords() + ".*"));
        query.limit(dto.getPageSize());
        return ResponseResult.okResult(mongoTemplate.find(query, ApAssociateWords.class));
    }
}
