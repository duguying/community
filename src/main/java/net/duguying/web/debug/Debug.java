package net.duguying.web.debug;

/**
 * Created by duguying on 2015/10/31.
 */
public class Debug {
    public static void println(String content){
        if (Config.DEV_MODE){
            System.out.println(content);
        }
    }
}
