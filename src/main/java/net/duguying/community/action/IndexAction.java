package net.duguying.community.action;

import net.duguying.community.tool.CommonTool;
import net.duguying.web.mvc.HttpAnnotation;
import net.duguying.web.mvc.RequestContext;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

/**
 * Created by duguying on 2015/10/31.
 */
public class IndexAction {
    @HttpAnnotation.URLMapping(uri = "/hello", method = "get")
    public void Index(RequestContext ctx) throws IOException {
        ctx.writeln("hello from Index method");

//        String root = ctx.root();
//        File dir = new File(root);
//        String parentDir = dir.getParent() + File.separator + "templates";

//        Properties p = new Properties();
//        p.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, parentDir);
//        VelocityEngine ve = new VelocityEngine();
//
//        ve.init(p);

//        Template t = ve.getTemplate("hellovelocity.vm");

//        VelocityContext vtx = new VelocityContext();
//        vtx.put("CommonTool",new CommonTool());

//        StringWriter writer = new StringWriter();
//        t.merge(vtx, writer);

//        ctx.write(writer.toString());
    }

    @HttpAnnotation.URLMapping(uri = "/test1")
    public void test1(RequestContext ctx) throws IOException {
        ctx.writeln("hello from test1 method");
    }
}
