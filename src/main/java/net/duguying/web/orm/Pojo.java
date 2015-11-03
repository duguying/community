package net.duguying.web.orm;

import net.duguying.web.tool.StringUtils;

import java.sql.SQLException;

/**
 * Created by duguying on 2015/10/30.
 */
public class Pojo {
    public String TableName(){
        return StringUtils.camelToUnderline(this.getClass().getSimpleName());
    }

    public <T extends Pojo> T Get(long id){
        String sql = "select * from "+TableName()+" where id=?";
        try {
            return (T)QueryHelper.read(this.getClass(),sql,id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
