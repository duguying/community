package net.duguying.web.orm;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by duguying on 2015/11/1.
 */
public class QueryHelper {
    public static <T> T read(Class<T> beanClass, String sql, Object... params) throws SQLException {
        Connection conn = DBManager.ME.getConnection();
        QueryRunner qr = new QueryRunner();
        return (T) qr.query(conn, sql, new BeanHandler(beanClass), params);
    }

    public static long create(String sql, Object... params){
        Connection conn = DBManager.ME.getConnection();
        QueryRunner qr = new QueryRunner();
        long id = 0;
        try {
            id = qr.insert(conn,sql,new ScalarHandler<Long>(),params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public static long update(String sql, Object... params){
        Connection conn = DBManager.ME.getConnection();
        QueryRunner qr = new QueryRunner();
        long result = 0;
        try {
            result = qr.update(conn,sql,params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] arg){
//        // read
//        Users user = null;
//        try {
//            user = read(Users.class, "select * from users where id=?", 8);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        System.out.println(user.getUsername());

        // create
        String sql = "insert users (username, password, salt, email, varified) value(?, ?, ?, ?, ?)";
        long id = create(sql, "test", "passwrod", "adsfasdf", "qer@asdfasdf.cn", "N");
        System.out.println(id);
    }
}
