package net.duguying.community.bean;
import net.duguying.web.orm.Pojo;

/**
 * Created by duguying on 2015/11/1.
 */
public class User extends Pojo {
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
}
