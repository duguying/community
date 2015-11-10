package net.duguying.web.orm;

import net.duguying.web.debug.Debug;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;
import org.apache.commons.lang.SystemUtils;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by duguying on 2015/11/1.
 */
public class QueryHelper {
    private final static Connection conn = DBManager.ME.getConnection();
    private final static QueryRunner qr = new QueryRunner();

    private final static ColumnListHandler _g_columnListHandler = new ColumnListHandler() {
        @Override
        protected Object handleRow(ResultSet rs) throws SQLException {
            Object obj = super.handleRow(rs);
            if (obj instanceof BigInteger)
                return ((BigInteger) obj).longValue();
            return obj;
        }

    };
    private final static ScalarHandler _g_scaleHandler = new ScalarHandler() {
        @Override
        public Object handle(ResultSet rs) throws SQLException {
            Object obj = super.handle(rs);
            if (obj instanceof BigInteger)
                return ((BigInteger) obj).longValue();
            return obj;
        }
    };

    public static <T> T read(Class<T> beanClass, String sql, Object... params) throws SQLException {
        System.out.println("[SQL] "+ new Date(System.currentTimeMillis()).toString() + " - " + sql);
        return (T) qr.query(conn, sql, new BeanHandler(beanClass), params);
    }

    public static long create(String sql, Object... params){
        long id = 0;
        try {
            System.out.println("[SQL] "+ new Date(System.currentTimeMillis()).toString() + " - " + sql);
            id = qr.insert(conn,sql,new ScalarHandler<Long>(),params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public static long update(String sql, Object... params){
        long result = 0;
        try {
            System.out.println("[SQL] "+ new Date(System.currentTimeMillis()).toString() + " - " + sql);
            result = qr.update(conn,sql,params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> List<T> query(Class<T> beanClass ,String sql, Object... params) throws SQLException {
        System.out.println("[SQL] "+ new Date(System.currentTimeMillis()).toString() + " - " + sql);
        return (List<T>) qr.query(conn, sql, _IsPrimitive(beanClass) ? _g_columnListHandler	: new BeanListHandler(beanClass), params);
    }

    public static long stat(String sql, Object... params) throws SQLException {
        System.out.println("[SQL] "+ new Date(System.currentTimeMillis()).toString() + " - " + sql);
        Number num = (Number) qr.query(conn, sql,	_g_scaleHandler, params);
        return (num != null) ? num.longValue() : -1;
    }

    private final static List<Class<?>> PrimitiveClasses = new ArrayList<Class<?>>() {
        {
            add(Long.class);
            add(Integer.class);
            add(Number.class);
            add(String.class);
            add(java.util.Date.class);
            add(java.sql.Date.class);
            add(java.sql.Timestamp.class);
        }
    };

    private final static boolean _IsPrimitive(Class<?> cls) {
        return cls.isPrimitive() || PrimitiveClasses.contains(cls);
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
