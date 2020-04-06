package com.erokhina.manager.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.erokhina.common.query.QueryDao;

public class QueryDaoImpl implements QueryDao {
    private Connection conn;

    public QueryDaoImpl(Connection conn) {
        this.conn = conn;
    }

    public String getSubscriptionInfo(Long uid) throws Exception {
        PreparedStatement st = null;
        ResultSet rs = null;

        st = conn.prepareStatement(
                "SELECT max(expiry_date) FROM membership_events WHERE user_id = ?");
        st.setLong(1, uid);
        rs = st.executeQuery();

        if (rs.next()) {
            return "Expiry date for uid " + uid + " is " + rs.getDate(1);
        }

        return "Error getting membership info";
    }
}
