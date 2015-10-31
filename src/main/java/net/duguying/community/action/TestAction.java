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
        ctx.write("hello world from Index method");
    }

    @HttpAnnotation.URLMapping(uri = "/param/:id/index/:time")
    public void param(RequestContext ctx) throws IOException {
        String id = ctx.uparam("id");
        int time = ctx.uparam("time",0);
        ctx.write("id: "+id);
        ctx.write("\ntime: "+time);
    }

    @HttpAnnotation.URLMapping(uri = "/[\\d]+")
    public void regexpRouter(RequestContext ctx) throws IOException {
        ctx.write("here is regexp router test, error");
    }

    @HttpAnnotation.URLMapping(uri = "/.*")
    public void regexpStarRouter(RequestContext ctx) throws IOException {
        ctx.write("here is regexp router test, star");
    }

    @HttpAnnotation.URLMapping(uri = "@404")
    public void page404(RequestContext ctx) throws IOException {
        ctx.write("404 page not found!");
    }
}
