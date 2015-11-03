package net.duguying.web.orm;

import net.duguying.community.bean.User;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by duguying on 2015/11/1.
 */
public class QueryHelper {
    public static <T> T Read(Class<T> beanClass, String sql, Object... params) throws SQLException {
        Connection conn = DBManager.ME.getConnection();
        QueryRunner qr = new QueryRunner();
        return (T) qr.query(conn, sql, new BeanHandler(beanClass), params);
    }

    public static void main(String[] arg){
        User user = null;
        try {
            user = Read(User.class, "select * from users where id=?", 8);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(user.getUsername());
    }
}
