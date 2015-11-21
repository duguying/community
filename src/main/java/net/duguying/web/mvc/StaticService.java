package net.duguying.web.mvc;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by duguying on 15/11/21.
 */
public class StaticService {
    public static boolean fileExist(String uri, RequestContext ctx){
        String webroot = ctx.webroot();
        String path = webroot + uri;
        File file = new File(path);
        return file.exists();
    }

    public static void response(String uri, RequestContext ctx) throws IOException {
        String webroot = ctx.webroot();
        String path = webroot + uri;
        File file = new File(path);
        byte[] bArr = FileUtils.readFileToByteArray(file);
        ctx.getResponse().getOutputStream().write(bArr);
    }
}
