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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                        this.scanMethod(classObj, requestMethod, uri);
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

    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this._do(request, response, "delete", request.getRequestURI());
    }

    public void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this._do(request, response, "head", request.getRequestURI());
    }

    public void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this._do(request, response, "options", request.getRequestURI());
    }

    public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this._do(request, response, "put", request.getRequestURI());
    }

    public void doTrace(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this._do(request, response, "trace", request.getRequestURI());
    }

    /**
     * scan and cache method url map
     * @param obj
     * @param requestMethod
     * @param uri
     */
    private void scanMethod(Object obj, String requestMethod, String uri){
        if (obj!=null){
            Method[] methods = obj.getClass().getDeclaredMethods();
            for(Method method : methods){
                Map<String, Object> action = new HashMap<String, Object>();

                HttpAnnotation.URLMapping um = method.getAnnotation(HttpAnnotation.URLMapping.class);
                if (um != null && um.uri().trim().length() > 0){
                    Debug.println("REQUEST: " + "[" + um.method().toUpperCase() + "]" + this.filtSpecialUriChars(um.uri()) +
                            " METHOD: " + obj.getClass().getName() + "." + method.getName());

                    action.put("HttpMethod", um.method().toLowerCase());
                    action.put("Class", obj);
                    action.put("Method", method);
                    this.URLMapping.put(this.filtSpecialUriChars(um.uri()), action);
                }
            }
        }
    }

    public String filtSpecialUriChars(String uri){
        return uri
//                .replaceAll("\\[","\\\\[").replaceAll("\\]","\\\\]")
//                .replaceAll("\\{","\\\\{").replaceAll("\\}","\\\\}")
                .replaceAll("\\(","\\\\(").replaceAll("\\)","\\\\)")
                .replaceAll("\\\\", "\\\\\\\\");
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
            if (reqMethod.equals(requestMethod) || reqMethod.equals("all")) {
                Method method = (Method) action.get("Method");
                Object _class = action.get("Class");
                method.invoke(_class, ctx);
            }
        }else {
            // regexp match mode
            for (Map.Entry<String, Object> entry : this.URLMapping.entrySet()) {
                String key = entry.getKey();
                action = (Map<String, Object>) entry.getValue();

                Map<String,Object> matchResult = this.matchSpecialAction(key, uri);
                if (matchResult!=null){
                    ctx.setURIParams(matchResult);
                    Method method = (Method) action.get("Method");
                    Object _class = action.get("Class");
                    method.invoke(_class, ctx);
                    return;
                }
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

    private Map<String,Object> matchSpecialAction(String uriMode, String reqUri){
        // ` :param `

        Pattern patternUriMode = Pattern.compile("/:([\\D]{1}[\\d\\D][^\\n\\r/]*)",Pattern.CASE_INSENSITIVE);
        Matcher matcherUriMode = patternUriMode.matcher(uriMode);

        Map<String,Object> params = new HashMap<String, Object>();
        uriMode = uriMode.replaceAll("/:[\\D]{1}[\\d\\D][^\\n\\r/]*","/([\\\\d\\\\D][^\\\\r\\\\n]*)");

        Pattern pattern = Pattern.compile(uriMode,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(reqUri);
        if(matcher.find()) {
            int count = matcher.groupCount();
            for (int i=0;i<count;i++){
                matcherUriMode.find();
                String key = matcherUriMode.group(1);
                String value = matcher.group(i+1);
                params.put(key, value);
            }
            return params;
        }else{
            return null;
        }
    }
}
