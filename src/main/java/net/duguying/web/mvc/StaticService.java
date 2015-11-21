package net.duguying.web.mvc;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by duguying on 15/11/21.
 */
public class StaticService {
    // TODO: 15/11/22 MIME header response
    public static Map<String,String> MIME = new HashMap<String,String>(){{
        put(".001","application/x-001");
    }};

    public static String mime(String uri){
        String ext = uri;
        String mime = MIME.get(ext);
        if (mime!=null){
            return mime;
        }else {
            return "application/octet-stream";
        }
    }

    public static boolean fileExist(String uri, RequestContext ctx){
        String webroot = ctx.webroot();
        String path = webroot + uri;
        File file = new File(path);
        return file.exists();
    }

    /**
     * static files response
     * @param uri
     * @param ctx
     * @throws IOException
     */
    public static void response(String uri, RequestContext ctx) throws IOException {
        String webroot = ctx.webroot();
        String path = webroot + uri;
        File file = new File(path);
        ctx.getResponse().setHeader("Content-Type", mime(uri));
        byte[] bArr = FileUtils.readFileToByteArray(file);
        ctx.getResponse().getOutputStream().write(bArr);
    }
}
