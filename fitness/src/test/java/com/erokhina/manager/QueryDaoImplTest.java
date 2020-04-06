package com.erokhina.manager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.erokhina.manager.query.QueryDaoImpl;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class QueryDaoImplTest {
    @Test
    public void testGetSubscriptionInfo() throws Exception {
        Connection connection = PowerMockito.mock(Connection.class);
        PreparedStatement st = PowerMockito.mock(PreparedStatement.class);
        ResultSet rs = PowerMockito.mock(ResultSet.class);

        when(connection.prepareStatement(Mockito.startsWith("SELECT"))).thenReturn(st);

        when(st.executeUpdate()).thenReturn(1);
        when(st.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getDate(1)).thenReturn(Date.valueOf("2020-03-03"));

        QueryDaoImpl queryDao = new QueryDaoImpl(connection);
        String result = queryDao.getSubscriptionInfo(7L);

        assertEquals(result, "Expiry date for uid 7 is 2020-03-03");

        verify(st, times(1)).setLong(1, 7L);
    }
}
