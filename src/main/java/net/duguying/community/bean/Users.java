package net.duguying.community.bean;
import net.duguying.web.orm.CacheAnnotation;
import net.duguying.web.orm.DBManager;
import net.duguying.web.orm.Pojo;
import net.duguying.web.orm.QueryHelper;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by duguying on 2015/11/1.
 */
public class Users extends Pojo {
    private long id;
    private String username;
    private String password;
    private String salt;
    private String email;
    private String varified;

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getSalt(){
        return salt;
    }

    public void setSalt(String salt){
        this.salt = salt;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getVarified(){
        return varified;
    }

    public void setVarified(String varified){
        this.varified = varified;
    }


    @CacheAnnotation.ListCache(tables = {"users", "user_log"})
    public List<Users> queryTop10() throws SQLException {
        String sql = "select id from "+TableName()+" limit 10";
        return LoadList(Users.class, Query(Long.class,sql));
    }

    @CacheAnnotation.ListCache(tables = {"users", "user_log"})
    public Long countAll() throws SQLException {
        String sql = "select count(id) from "+TableName();
        return this.Stat(sql);
    }


    public static void main(String[] arg){
        Users user = new Users();
        try {
            List<Users> list1 = user.queryTop10();
            for (Object item : list1) {
                System.out.println(item.toString());
            }
            System.out.println("=== end ===");
            List<Users> list2 = user.queryTop10();
            for (Object item : list2) {
                System.out.println(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
