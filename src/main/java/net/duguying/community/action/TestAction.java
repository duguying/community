package net.duguying.community.action;

import net.duguying.web.mvc.HttpAnnotation;
import net.duguying.web.mvc.RequestContext;

import java.io.IOException;

/**
 * Created by duguying on 2015/10/31.
 */
public class TestAction {
    @HttpAnnotation.URLMapping(uri = "/hello/world", method = "get")
    public void Index(RequestContext ctx) throws IOException {
        System.out.println("hello world from Index method");
        ctx.write("hello world");
    }
}
