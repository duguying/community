package net.duguying.web.mvc;

import net.duguying.web.debug.Debug;

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
    private Map<String, Object> URLMapping = new HashMap<String, Object>();
    
    /**
     * do for request
     * @param request
     * @param response
     * @throws IOException
     */
    private void _do(HttpServletRequest request, HttpServletResponse response, String requestMethod, String uri) throws IOException {
        RequestContext ctx = new RequestContext(request, response);
        // get URLMapping
        if (this.URLMapping.size()<=0) {
            String packages = this.getInitParameter("packages");
            String[] packageList = this.parsePackages(packages);
            for (String packageName : packageList) {
                String[] classNameList = this.parseClasses(packageName);
                for (String className : classNameList) {
                    Object classObj = null;
                    try {
                        classObj = this.loadClass(className);
                        this.scanMethod(classObj, ctx, requestMethod, uri);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        try {
            this.execMethod(uri, requestMethod, ctx);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
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
     * scan and cache method url map
     * @param obj
     * @param ctx
     * @param requestMethod
     * @param uri
     */
    private void scanMethod(Object obj, RequestContext ctx, String requestMethod, String uri){
        if (obj!=null){
            Method[] methods = obj.getClass().getDeclaredMethods();
            for(Method method : methods){
                Map<String, Object> action = new HashMap<String, Object>();

                HttpAnnotation.URLMapping um = method.getAnnotation(HttpAnnotation.URLMapping.class);
                if (um != null && um.uri().trim().length() > 0){
                    Debug.println("REQUEST: " + "[" + um.method().toUpperCase() + "]" + um.uri() +
                            " METHOD: " + obj.getClass().getName() + "." + method.getName());

                    action.put("HttpMethod", um.method().toLowerCase());
                    action.put("Class", obj);
                    action.put("Method", method);
                    this.URLMapping.put(um.uri(), action);
                }
            }
        }
    }

    /**
     * execute method
     * @param uri
     * @param requestMethod
     * @param ctx
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void execMethod(String uri, String requestMethod, RequestContext ctx) throws InvocationTargetException, IllegalAccessException {
        Map<String, Object> action = (Map<String, Object>) this.URLMapping.get(uri);
        if (action!=null) {
            String reqMethod = (String) action.get("HttpMethod");
            if (reqMethod.equals(requestMethod)) {
                Method method = (Method) action.get("Method");
                Object _class = action.get("Class");
                method.invoke(_class, ctx);
            }
        }
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
