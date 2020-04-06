package com.erokhina.report.query.queries;

import java.time.LocalDate;

import com.erokhina.common.query.Query;
import com.erokhina.common.query.QueryDao;
import com.erokhina.report.query.QueryDaoImpl;

public class GetStatsQuery implements Query {
    LocalDate from;
    LocalDate to;

    public GetStatsQuery(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String process(QueryDao queryDao) throws Exception {
        return ((QueryDaoImpl)queryDao).getReport(from, to);
    }
}
