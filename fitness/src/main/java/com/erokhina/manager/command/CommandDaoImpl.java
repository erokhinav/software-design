package com.erokhina.manager.command;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.erokhina.common.command.CommandDao;

public class CommandDaoImpl implements CommandDao {
    private Connection conn;

    public CommandDaoImpl(Connection conn) {
        this.conn = conn;
    }

    public String addNewUser() throws Exception {
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users DEFAULT VALUES",
                Statement.RETURN_GENERATED_KEYS);

        int affectedRows = pstmt.executeUpdate();

        long uid = -1;
        if (affectedRows > 0) {
            // get user id
            ResultSet rs = pstmt.getGeneratedKeys();

            if (rs.next()) {
                uid = rs.getLong(1);
            }

            if (uid != -1) {
                return "New uid is " + uid;
            }
        }

        // rollback the transaction if the insert failed
        conn.rollback();
        return "Error while inserting new user";
    }

    public String extendSubscription(Long uid, Date expiryDate) throws Exception {
        PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO membership_events(user_id, expiry_date) VALUES(?, ?) ");

        pstmt.setLong(1, uid);
        pstmt.setDate(2, expiryDate);

        int affectedRows = pstmt.executeUpdate();

        if (affectedRows > 0) {
            return "Subscription was successfully extended.";
        }

        // rollback the transaction if the insert failed
        conn.rollback();
        return "Error while extending subscription.";
    }
}
