package net.duguying.web.mvc;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by duguying on 15/11/21.
 */
public class StaticService {
    // TODO: 15/11/22 MIME header response
    public static Map<String,String> MIME = new HashMap<String,String>(){{
        put(".001","application/x-001");
        put(".323","text/h323");
        put(".907","drawing/907");
        put(".acp","audio/x-mei-aac");
        put(".aif","audio/aiff");
        put(".aiff","audio/aiff");
        put(".asa","text/asa");
        put(".asp","text/asp");
        put(".au","audio/basic");
        put(".awf","application/vnd.adobe.workflow");
        put(".bmp","application/x-bmp");
        put(".c4t","application/x-c4t");
        put(".cal","application/x-cals");
        put(".cdf","application/x-netcdf");
        put(".cel","application/x-cel");
        put(".cg4","application/x-g4");
        put(".cit","application/x-cit");
        put(".cml","text/xml");
        put(".cmx","application/x-cmx");
        put(".crl","application/pkix-crl");
        put(".csi","application/x-csi");
        put(".cut","application/x-cut");
        put(".dbm","application/x-dbm");
        put(".dcd","text/xml");
        put(".der","application/x-x509-ca-cert");
        put(".dib","application/x-dib");
        put(".doc","application/msword");
        put(".drw","application/x-drw");
        put(".dwf","Model/vnd.dwf");
        put(".dwg","application/x-dwg");
        put(".dxf","application/x-dxf");
        put(".emf","application/x-emf");
        put(".ent","text/xml");
        put(".eps","application/x-ps");
        put(".etd","application/x-ebx");
        put(".fax","image/fax");
        put(".fif","application/fractals");
        put(".frm","application/x-frm");
        put(".gbr","application/x-gbr");
        put(".gif","image/gif");
        put(".gp4","application/x-gp4");
        put(".hmr","application/x-hmr");
        put(".hpl","application/x-hpl");
        put(".hrf","application/x-hrf");
        put(".htc","text/x-component");
        put(".html","text/html");
        put(".htx","text/html");
        put(".ico","image/x-icon");
        put(".iff","application/x-iff");
        put(".igs","application/x-igs");
        put(".img","application/x-img");
        put(".isp","application/x-internet-signup");
        put(".java","java/*");
        put(".jpe","image/jpeg");
        put(".jpeg","image/jpeg");
        put(".jpg","application/x-jpg");
        put(".jsp","text/html");
        put(".lar","application/x-laplayer-reg");
        put(".lavs","audio/x-liquid-secure");
        put(".lmsff","audio/x-la-lms");
        put(".ltr","application/x-ltr");
        put(".m2v","video/x-mpeg");
        put(".m4e","video/mpeg4");
        put(".man","application/x-troff-man");
        put(".mdb","application/msaccess");
        put(".mfp","application/x-shockwave-flash");
        put(".mhtml","message/rfc822");
        put(".mid","audio/mid");
        put(".mil","application/x-mil");
        put(".mnd","audio/x-musicnet-download");
        put(".mocha","application/x-javascript");
        put(".mp1","audio/mp1");
        put(".mp2v","video/mpeg");
        put(".mp4","video/mpeg4");
        put(".mpd","application/vnd.ms-project");
        put(".mpeg","video/mpg");
        put(".mpga","audio/rn-mpeg");
        put(".mps","video/x-mpeg");
        put(".mpv","video/mpg");
        put(".mpw","application/vnd.ms-project");
        put(".mtx","text/xml");
        put(".net","image/pnetvue");
        put(".nws","message/rfc822");
        put(".out","application/x-out");
        put(".p12","application/x-pkcs12");
        put(".p7c","application/pkcs7-mime");
        put(".p7r","application/x-pkcs7-certreqresp");
        put(".pc5","application/x-pc5");
        put(".pcl","application/x-pcl");
        put(".pdf","application/pdf");
        put(".pdx","application/vnd.adobe.pdx");
        put(".pgl","application/x-pgl");
        put(".pko","application/vnd.ms-pki.pko");
        put(".plg","text/html");
        put(".plt","application/x-plt");
        put(".png","application/x-png");
        put(".ppa","application/vnd.ms-powerpoint");
        put(".pps","application/vnd.ms-powerpoint");
        put(".ppt","application/x-ppt");
        put(".prf","application/pics-rules");
        put(".prt","application/x-prt");
        put(".ps","application/postscript");
        put(".pwz","application/vnd.ms-powerpoint");
        put(".ra","audio/vnd.rn-realaudio");
        put(".ras","application/x-ras");
        put(".rdf","text/xml");
        put(".red","application/x-red");
        put(".rjs","application/vnd.rn-realsystem-rjs");
        put(".rlc","application/x-rlc");
        put(".rm","application/vnd.rn-realmedia");
        put(".rmi","audio/mid");
        put(".rmm","audio/x-pn-realaudio");
        put(".rms","application/vnd.rn-realmedia-secure");
        put(".rmx","application/vnd.rn-realsystem-rmx");
        put(".rp","image/vnd.rn-realpix");
        put(".rsml","application/vnd.rn-rsml");
        put(".rtf","application/msword");
        put(".rv","video/vnd.rn-realvideo");
        put(".sat","application/x-sat");
        put(".sdw","application/x-sdw");
        put(".slb","application/x-slb");
        put(".slk","drawing/x-slk");
        put(".smil","application/smil");
        put(".snd","audio/basic");
        put(".sor","text/plain");
        put(".spl","application/futuresplash");
        put(".ssm","application/streamingmedia");
        put(".stl","application/vnd.ms-pki.stl");
        put(".sty","application/x-sty");
        put(".swf","application/x-shockwave-flash");
        put(".tg4","application/x-tg4");
        put(".tif","image/tiff");
        put(".tiff","image/tiff");
    }};

    public static String mime(String uri){
        Pattern p = Pattern.compile("\\.[\\d\\D][^\\.]*");
        Matcher m = p.matcher(uri);
        if(m.find()){
            String ext = m.group();
            String mime = MIME.get(ext);
            if (mime!=null){
                return mime;
            }else {
                return "application/octet-stream";
            }
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

    public static void main(String[] args){
        Pattern p = Pattern.compile("\\.[\\d\\D][^\\.]*");
        Matcher m = p.matcher("abc.asd");
        m.find();
        System.out.println(m.group());
    }
}
