package net.duguying.community.bean;
import net.duguying.web.orm.Pojo;

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


    public static void main(String[] arg){
//        Users user = new Users();
//        Users result = user.Get(8);
//        System.out.println(result.getUsername());
        Users user = new Users();
        user.setUsername("rex1");
        user.setEmail("root@duguying.net");
        user.setPassword("asdfakldsfja");
        user.setSalt("asdf");
        user.setVarified("N");

        long id =user.Save();
        System.out.println(id);
    }
}
