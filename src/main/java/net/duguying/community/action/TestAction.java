package net.duguying.community.action;

import net.duguying.o.mvc.HttpAnnotation;
import net.duguying.o.mvc.RequestContext;

import java.io.IOException;

/**
 * Created by duguying on 2015/10/31.
 */
public class TestAction {
    @HttpAnnotation.URLMapping(uri = "/hello/world", method = "get")
    public void Index(RequestContext ctx) throws IOException {
        String str = ctx.param("str","empty");
        int intVal = ctx.param("int",0);
        long longVal = ctx.param("long",1111111111);
        float floatVal = ctx.param("float",0.123f);

        ctx.writeln("[str]"+str);
        ctx.writeln("[int]"+intVal);
        ctx.writeln("[long]"+longVal);
        ctx.writeln("[float]"+floatVal);

        ctx.writeln("hello world from Index method");
    }

    @HttpAnnotation.URLMapping(uri = "/param/:id/index/:time")
    public void param(RequestContext ctx) throws IOException {
        String id = ctx.uparam("id");
        int time = ctx.uparam("time",0);
        ctx.writeln("id: "+id);
        ctx.writeln("time: "+time);
    }

    @HttpAnnotation.URLMapping(uri = "/[\\d]+")
    public void regexpRouter(RequestContext ctx) throws IOException {
        ctx.writeln("here is regexp router test, error");
    }

//    @HttpAnnotation.URLMapping(uri = "/.*")
//    public void regexpStarRouter(RequestContext ctx) throws IOException {
//        ctx.writeln("here is regexp router test, star");
//    }

    @HttpAnnotation.URLMapping(uri = "@404")
    public void page404(RequestContext ctx) throws IOException {
        ctx.writeln("404 page not found! Orz...");
    }
}
