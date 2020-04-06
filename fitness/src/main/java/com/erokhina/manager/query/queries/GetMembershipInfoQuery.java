package com.erokhina.manager.query.queries;

import com.erokhina.common.query.Query;
import com.erokhina.common.query.QueryDao;
import com.erokhina.manager.query.QueryDaoImpl;

public class GetMembershipInfoQuery implements Query {
    private Long uid;

    public GetMembershipInfoQuery(Long uid) {
        this.uid = uid;
    }

    @Override
    public String process(QueryDao queryDao) throws Exception {
        return ((QueryDaoImpl)queryDao).getSubscriptionInfo(uid);
    }
}
