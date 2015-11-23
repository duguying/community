package net.duguying.community.bean;

import net.duguying.o.orm.Pojo;

import java.sql.Date;

/**
 * Created by duguying on 2015/11/9.
 */
public class UserLog extends Pojo {
    private long id;
    private long user;
    private String ip;
    private String ua;
    private String location;
    private int action;
    private Date create_time;

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getUser(){
        return user;
    }

    public void setUser(long user){
        this.user = user;
    }

    public String getIp(){
        return ip;
    }

    public void setIp(String ip){
        this.ip = ip;
    }

    public String getUa(){
        return ua;
    }

    public void setUa(String ua){
        this.ua = ua;
    }

    public String getLocation(){
        return location;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public int getAction(){
        return action;
    }

    public void setAction(int action){
        this.action = action;
    }

    public Date getCreate_time(){
        return create_time;
    }

    public void setCreate_time(Date create_time){
        this.create_time = create_time;
    }
}
