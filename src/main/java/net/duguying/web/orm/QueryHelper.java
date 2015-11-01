package net.duguying.web.orm;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by duguying on 2015/11/1.
 */
public class QueryHelper {
    public <T> T Read(Class<T> beanClass, String sql, Object... params) throws SQLException {
        Connection conn = DBManager.ME.getConnection();
        QueryRunner qr = new QueryRunner();
        return (T) qr.query(conn, sql, new BeanHandler(beanClass), params);
    }
}
