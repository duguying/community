package net.duguying.community.bean;
import net.duguying.web.orm.CacheAnnotation;
import net.duguying.web.orm.DBManager;
import net.duguying.web.orm.Pojo;
import net.duguying.web.orm.QueryHelper;

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


    @CacheAnnotation.ListCache
    public List<Long> queryTop10(){
        String sql = "select id from "+TableName()+" limit 10";
        return this.query(sql);
    }


    public static void main(String[] arg){
        Users user = new Users();
//        Users result = user.Get(9);
//        result.setPassword("123456");
//        boolean r = result.Update();
//        System.out.println(result.getUsername());
//        Users u2 = new Users();
//        Users r2 = u2.Get(9);
//        System.out.println(r2.getUsername());
//        r2.setUsername("change");
//        r2.Update();
//        Users r3 = u2.Get(9);
//        System.out.println(r3.getUsername());
        user.queryTop10();
    }
}
