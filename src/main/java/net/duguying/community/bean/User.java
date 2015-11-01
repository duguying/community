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
}
