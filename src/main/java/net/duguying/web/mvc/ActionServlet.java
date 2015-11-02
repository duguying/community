package net.duguying.web.mvc;

import net.duguying.community.tool.CommonTool;
import net.duguying.web.debug.Debug;
import net.duguying.web.orm.DBManager;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by duguying on 2015/10/30.
 */
public class ActionServlet extends HttpServlet {
    private Map<String, Object> URLMapping = new HashMap<String, Object>();
    private String TPL_DIR;
    private VelocityEngine VE = null;
    private VelocityContext VTX = null;
    private String PREFIX = "";
    private Map<String,Object> VelocityTools = new HashMap<String, Object>();

    public void init() throws ServletException {
        super.init();

        // hello world
        Debug.println("!!!  HELLO WORLD  !!!");

        // action prefix
        String prefix = this.getInitParameter("action-prefix");
        if (prefix!=null){
            this.PREFIX = prefix;
        }

        // scan uri mapping
        this.scanMethod();

        // initial velocity
        this.initVelocity();
        this.loadVelocityTools();

        // initial database connection
//        DBManager.ME.getConnection();

    }
    
    /**
     * do for request
     * @param request
     * @param response
     * @throws IOException
     */
    private void _do(HttpServletRequest request, HttpServletResponse response, String requestMethod, String uri) throws IOException {
        RequestContext ctx = new RequestContext(request, response);

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
     */
    private void scanMethod() {

        // get URLMapping
        if (this.URLMapping.size()<=0) {
            String packages = this.getInitParameter("packages");
            String[] packageList = this.parsePackages(packages);
            for (String packageName : packageList) {
                String[] classNameList = this.parseClasses(packageName);
                for (String className : classNameList) {
                    // load class
                    Object classObj = null;
                    try {
                        classObj = this.loadClass(className);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }

                    // parse mapping and cache
                    if (classObj!=null){
                        Method[] methods = classObj.getClass().getDeclaredMethods();
                        for(Method method : methods){
                            Map<String, Object> action = new HashMap<String, Object>();

                            HttpAnnotation.URLMapping um = method.getAnnotation(HttpAnnotation.URLMapping.class);
                            if (um != null && um.uri().trim().length() > 0){
                                Debug.println("[URI_MAPPING] REQUEST: " + "[" + um.method().toUpperCase() + "]" + this.filtSpecialUriChars(um.uri()) +
                                        " METHOD: " + classObj.getClass().getName() + "." + method.getName());

                                action.put("HttpMethod", um.method().toLowerCase());
                                action.put("Class", classObj);
                                action.put("Method", method);
                                this.URLMapping.put(this.filtSpecialUriChars(um.uri()), action);
                            }
                        }
                    }

                }
            }
        }



    }

    public String filtSpecialUriChars(String uri){
        String prefix = "";
        if (!this.PREFIX.isEmpty()){
            prefix = "/" + this.PREFIX;
        }
        return prefix + uri
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
    public void execMethod(String uri, String requestMethod, RequestContext ctx) throws InvocationTargetException, IllegalAccessException, IOException {
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

            // match velocity mode
            if(this.hasVelocityTemplate(uri)){
                this.renderVelocityTemplate(uri, ctx);
                return;
            }

        }
    }

    /**
     * load class
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

    /**
     * parse packages from configuration
     * @param packages
     * @return
     */
    private String[] parsePackages(String packages){
        return packages.split(",");
    }

    /**
     * parse classes from package
     * @param packageName
     * @return
     */
    private String[] parseClasses(String packageName){
        List<String> fileNameList = new ArrayList<String>();
        String dir = RequestContext.root()+ File.separator +packageName.replaceAll("\\.","/");
        File file = new File(dir);
        File[] list = file.listFiles();
        for (File f : list){
            String filename = f.getName();
            if (filename.endsWith(".class")){
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
        uriMode = uriMode.replaceAll("/:[\\D]{1}[\\d\\D][^\\n\\r/]*","/([\\\\d\\\\D][^\\\\r\\\\n]*)")+"$";

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

    /**
     * initial velocity engine
     */
    private void initVelocity(){
        String templatesDirName = this.getInitParameter("velocity-template");
        this.TPL_DIR = (new File(RequestContext.root())).getParent() + File.separator + templatesDirName;
        Properties p = new Properties();
        p.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, this.TPL_DIR);
        p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        this.VE = new VelocityEngine();
        this.VE.init(p);
    }

    /**
     * is uri mapping velocity file exist
     * @param uri
     * @return
     */
    private boolean hasVelocityTemplate(String uri){
        // file or directory start with _ ignored
        if (uri.contains("/_")){
            return false;
        }

        String uriPath = uri.replace("/", File.separator);
        String templatesFile = this.TPL_DIR + uriPath + ".vm";
        File file = new File(templatesFile);
        if(file.exists()){
            return true;
        }else {
            String indexFile = templatesFile.replaceAll("\\.vm$", File.separator + "index.vm");
            File idxFile = new File(indexFile);
            return idxFile.exists();
        }
    }

    /**
     * load velocity tools
     */
    private void loadVelocityTools() {
        this.VTX = new VelocityContext();

        if (this.VelocityTools.size()<=0){
            String packages = this.getInitParameter("velocity-tool");
            String[] packageArray = this.parsePackages(packages);
            for (String packageName : packageArray){
                String[] classArray = this.parseClasses(packageName);
                for (String className : classArray){
                    // load class
                    Object classObject = null;
                    try {
                        classObject = this.loadClass(className);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }

                    if (classObject!=null){
                        VelocityTools.put(classObject.getClass().getSimpleName(),classObject);
                    }

                }
            }
        }

        for (Map.Entry<String, Object> entry : this.VelocityTools.entrySet()) {
            String key = entry.getKey();
            Object classObject = entry.getValue();
            this.VTX.put(key,classObject);
        }
    }

    /**
     * rendering velocity templates
     * @param uri
     */
    private void renderVelocityTemplate(String uri, RequestContext ctx) throws IOException {
        String templatesFile = this.TPL_DIR + uri.replace("/", File.separator) + ".vm";
        File file = new File(templatesFile);
        Template t = null;
        if(!file.exists()){
            String indexFile = templatesFile.replaceAll("\\.vm$", File.separator + "index.vm");
            file = new File(indexFile);
            if (file.exists()){
                t = this.VE.getTemplate(uri.replace("/", File.separator) + File.separator + "index.vm");
            }
        }else{
            t = this.VE.getTemplate(uri.replace("/", File.separator) + ".vm");
        }

        if (t!=null){
            StringWriter writer = new StringWriter();
            t.merge(this.VTX,writer);
            ctx.write(writer.toString());
        }
    }
}
