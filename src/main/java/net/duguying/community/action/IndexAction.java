package net.duguying.community.action;

import net.duguying.web.mvc.HttpAnnotation;
import net.duguying.web.mvc.RequestContext;

import java.io.IOException;

/**
 * Created by duguying on 2015/10/31.
 */
public class IndexAction {
    @HttpAnnotation.URLMapping(uri = "/hello", method = "get")
    public void Index(RequestContext ctx) throws IOException {
        System.out.println("hello from Index method");
        ctx.write("hello world");
    }

    @HttpAnnotation.URLMapping(uri = "/test1")
    public void test1(RequestContext ctx){
        System.out.println("hello from test1 method");
    }
}
