package net.duguying.community.tool;

import net.duguying.community.bean.Users;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by duguying on 2015/10/31.
 */
public class CommonTool {
    public static String test(){
        return "hello world, from velocity tool method";
    }

    public static List<Users> queryTop10(){
        return Users.ME.queryTop10();
    }
}
