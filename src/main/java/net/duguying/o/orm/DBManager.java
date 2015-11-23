package net.duguying.o.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by duguying on 2015/11/1.
 */
public class DBManager {
    private String url = "jdbc:mysql://127.0.0.1:3306/blog";
    private String name = "com.mysql.jdbc.Driver";
    private String user = "root";
    private String password = "lijun";
    private int MAX_CONN = 10;
    private int request_connection_times = 0;

    public static DBManager ME = new DBManager();
    public List<Connection> pool = new ArrayList<Connection>();

    public DBManager(){
        // initial connection pool
        for (int i = 0; i < this.MAX_CONN; i++){
            try {
                Connection conn = this.createConnection();
                if (conn != null){
                    this.pool.add(conn);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private Connection createConnection() throws SQLException, ClassNotFoundException {
        Class.forName(name);
        return DriverManager.getConnection(url, user, password);
    }

    private void checkConnectionPool() {
        for (int i = 0; i < this.pool.size(); i++) {
            Connection conn = this.pool.get(i);
            try {
                if (conn.isClosed()){
                    this.pool.remove(i);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        int addNum = this.MAX_CONN - this.pool.size();
        for (int idx = 0; idx < addNum; idx++){
            Connection conn = null;
            try {
                conn = createConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (conn != null){
                this.pool.add(conn);
            }
        }
    }

    /**
     * get a connection from connection pool
     * @return
     */
    public Connection getConnection(){
        int idx = request_connection_times % this.MAX_CONN + 1;
        Connection conn = this.pool.get(idx);
        request_connection_times++;
        this.checkConnectionPool();
        return conn;
    }
}
