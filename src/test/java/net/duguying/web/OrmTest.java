package net.duguying.web;

import junit.framework.TestCase;
import net.duguying.community.bean.UserLog;
import net.duguying.community.bean.Users;

import java.sql.Date;
import java.sql.SQLException;

public class OrmTest extends TestCase {
    protected void setUp() {
    }

    public void testOrm(){
        // create user list first
//        Users users = new Users();
//        try {
//            users.queryTop10();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

//        UserLog userLog = new UserLog();
//        userLog.setAction(1);
//        userLog.setIp("127.0.0.1");
//        userLog.setLocation("unknown");
//        userLog.setUa("firefox");
//        userLog.setUser(9);
//        userLog.setCreate_time(new Date(System.currentTimeMillis()));
//        userLog.Save();

//        try {
//            users.queryTop10();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            long count1 = users.countAll();
//            System.out.println(count1);
//
//            long count2 = users.countAll();
//            System.out.println(count2);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }
}