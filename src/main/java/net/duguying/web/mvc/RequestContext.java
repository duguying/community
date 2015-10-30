package net.duguying.web.mvc;

import net.duguying.web.debug.Debug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by duguying on 2015/10/31.
 */
public class RequestContext {
    private HttpServletRequest request;
    private HttpServletResponse response;

    public RequestContext(HttpServletRequest request, HttpServletResponse response){
        this.request = request;
        this.response = response;
    }

    public void test(){
        Debug.println("hello from RequestContext");
    }

    public HttpServletRequest getRequest(){
        return this.request;
    }

    public HttpServletResponse getResponse(){
        return this.response;
    }

    public void write(String content) throws IOException {
        PrintWriter out = response.getWriter();
        out.println(content);
    }
}
