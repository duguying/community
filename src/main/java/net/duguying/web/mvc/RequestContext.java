package net.duguying.web.mvc;

import net.duguying.web.debug.Debug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by duguying on 2015/10/31.
 */
public class RequestContext {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private static String root;
    private String uri;
    private Map<String,Object> URIParams;
    private String header = "text/html; charset=utf-8";

    public RequestContext(HttpServletRequest request, HttpServletResponse response){
        this.request = request;
        this.response = response;
        this.uri = request.getRequestURI();
    }

    public HttpServletRequest getRequest(){
        return this.request;
    }

    public HttpServletResponse getResponse(){
        return this.response;
    }

    public final static String root() {
        if(root==null || root.trim().length()>0){
            root = RequestContext.class.getResource("/").getFile();
        }
        return root;
    }

    public final static String webroot() {
        String rootpath = root();
        return new File(new File(rootpath).getParent()).getParent();
    }

    public void write(String content) throws IOException {
        response.setContentType(this.header);
        PrintWriter out = response.getWriter();
        out.print(content);
    }

    public void writeln(String content) throws IOException {
        response.setContentType(this.header);
        PrintWriter out = response.getWriter();
        out.println(content);
    }

    /**
     * parse uri params
     * @param key
     * @return
     */
    public String uparam(String key){
        Object value = this.URIParams.get(key);
        if (value!=null){
            return value.toString();
        }else {
            return null;
        }
    }

    /**
     * parse uri params with default value
     * @param key
     * @param defaultValue
     * @return
     */
    public String uparam(String key, String defaultValue){
        Object value = this.URIParams.get(key);
        if (value!=null){
            return value.toString();
        }else {
            return defaultValue;
        }
    }

    public int uparam(String key, int defaultValue){
        Object value = this.URIParams.get(key);
        if (value!=null){
            return Integer.parseInt(value.toString());
        }else {
            return defaultValue;
        }
    }

    public long uparam(String key, long defaultValue){
        Object value = this.URIParams.get(key);
        if (value!=null){
            return Long.parseLong(value.toString());
        }else {
            return defaultValue;
        }
    }

    public float uparam(String key, float defaultValue){
        Object value = this.URIParams.get(key);
        if (value!=null){
            return Float.parseFloat(value.toString());
        }else {
            return defaultValue;
        }
    }

    /**
     * put the uri params into RequestContext
     * @param uparam
     */
    protected void setURIParams(Map<String,Object> uparam){
        this.URIParams = uparam;
    }

    public String param(String key){
        return this.request.getParameter(key);
    }

    public String param(String key, String defaultValue){
        String value = this.request.getParameter(key);
        if (value != null){
            return value;
        }else {
            return defaultValue;
        }
    }

    public int param(String key, int defaultValue){
        String value = this.request.getParameter(key);
        if (value != null){
            return Integer.parseInt(value);
        }else {
            return defaultValue;
        }
    }

    public long param(String key, long defaultValue){
        String value = this.request.getParameter(key);
        if (value != null){
            return Long.parseLong(value);
        }else {
            return defaultValue;
        }
    }

    public float param(String key, float defaultValue){
        String value = this.request.getParameter(key);
        if (value != null){
            return Float.parseFloat(value);
        }else {
            return defaultValue;
        }
    }

    public boolean param(String key, boolean defaultValue){
        String value = this.request.getParameter(key);
        if (value != null){
            return value.equals("true");
        }else {
            return defaultValue;
        }
    }
}
