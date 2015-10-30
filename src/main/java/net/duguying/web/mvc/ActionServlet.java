package net.duguying.web.mvc;

import net.duguying.web.debug.Debug;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by duguying on 2015/10/30.
 */
public class ActionServlet extends HttpServlet {
    /**
     * do for request
     * @param request
     * @param response
     * @throws IOException
     */
    private void _do(HttpServletRequest request, HttpServletResponse response, String requestMethod, String uri) throws IOException {
        RequestContext ctx = new RequestContext(request, response);
        try {
            Object obj = loadClass("net.duguying.community.action.IndexAction");
            if (obj!=null){
                Method[] methods = obj.getClass().getDeclaredMethods();
                for(Method method : methods){
                    HttpAnnotation.URLMapping um = method.getAnnotation(HttpAnnotation.URLMapping.class);
                    if (um != null){
                        if (um.uri().toLowerCase().equals(uri)){
                            if (um.method().toLowerCase().equals(requestMethod)){
                                Debug.println("REQUEST: " + um.method() + "," + um.uri() + " METHOD: " + method.getName());
                                method.invoke(obj, ctx);
                            }else if(um.method().toLowerCase().equals("all")){
                                Debug.println("REQUEST: "+um.method()+","+um.uri()+" METHOD: "+method.getName());
                                method.invoke(obj, ctx);
                            }
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this._do(request, response, "get", request.getRequestURI());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this._do(request, response, "post", request.getRequestURI());
    }

    /**
     * 加载类
     * @param cls
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private Object loadClass(String cls) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Object action = Class.forName(cls).newInstance();
        return action;
    }
}
