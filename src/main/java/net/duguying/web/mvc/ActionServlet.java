package net.duguying.web.mvc;

import net.duguying.web.debug.Debug;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // load class
        List<Object> classObjects = new ArrayList<Object>();
        String packages = this.getInitParameter("packages");
        String[] packageList = this.parsePackages(packages);
        for (String packageName : packageList){
            String[] classNameList = this.parseClasses(packageName);
            for (String className : classNameList){
                Object classObj = null;
                try {
                    classObj = this.loadClass(className);
                    classObjects.add(classObj);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Object obj : classObjects){
            if(this.execMethod(obj,ctx,requestMethod,uri)){
                break;
            }
        }

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this._do(request, response, "get", request.getRequestURI());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this._do(request, response, "post", request.getRequestURI());
    }

    private boolean execMethod(Object obj, RequestContext ctx, String requestMethod, String uri){
        try {
            if (obj!=null){
                Method[] methods = obj.getClass().getDeclaredMethods();
                for(Method method : methods){
                    HttpAnnotation.URLMapping um = method.getAnnotation(HttpAnnotation.URLMapping.class);
                    if (um != null){
                        if (um.uri().toLowerCase().equals(uri)){
                            if (um.method().toLowerCase().equals(requestMethod)){
                                Debug.println("REQUEST: " + um.method() + "," + um.uri() + " METHOD: " + method.getName());
                                method.invoke(obj, ctx);
                                return true;
                            }else if(um.method().toLowerCase().equals("all")){
                                Debug.println("REQUEST: "+um.method()+","+um.uri()+" METHOD: "+method.getName());
                                method.invoke(obj, ctx);
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
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

    private String[] parsePackages(String packages){
        return packages.split(",");
    }

    private String[] parseClasses(String packageName){
        List<String> fileNameList = new ArrayList<String>();
        String dir = RequestContext.root()+ File.separator +packageName.replaceAll("\\.","/");
        File file = new File(dir);
        File[] list = file.listFiles();
        for (File f : list){
            String filename = f.getName();
            if (filename.endsWith("Action.class")){
                String className = filename.replace(".class","");
                fileNameList.add(packageName+"."+className);
            }
        }

        String[] a = new String[0];
        return fileNameList.toArray(a);
    }
}
