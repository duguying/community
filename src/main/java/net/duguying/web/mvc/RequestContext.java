package net.duguying.web.mvc;

import net.duguying.web.debug.Debug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by duguying on 2015/10/31.
 */
public class RequestContext {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private static String root;

    public RequestContext(HttpServletRequest request, HttpServletResponse response){
        this.request = request;
        this.response = response;
    }

    public HttpServletRequest getRequest(){
        return this.request;
    }

    public HttpServletResponse getResponse(){
        return this.response;
    }

    public final static String root() {
        if(root!=null && root.trim().length()>0){
            return root;
        }
        return RequestContext.class.getResource("/").getFile();
    }

    public void write(String content) throws IOException {
        PrintWriter out = response.getWriter();
        out.println(content);
    }
}
