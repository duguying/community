package net.duguying.o.orm;

import net.duguying.o.cache.CacheManage;
import net.duguying.o.tool.StringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.dbutils.DbUtils;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by duguying on 2015/10/30.
 */
public class Pojo {
    private int id;
    private long _key_id;

    public String TableName(){
        return StringUtils.camelToUnderline(this.getClass().getSimpleName());
    }

    /**
     * Get method
     * @param id
     * @param <T>
     * @return
     */
    public <T extends Pojo> T Get(long id){
        T cachedObject = (T) CacheManage.ME.get(TableName(), "Object"+id);
        if (cachedObject != null){
            return cachedObject;
        }

        String sql = "select * from "+TableName()+" where id=?";
        try {
            T object = (T)QueryHelper.read(this.getClass(),sql,id);
            CacheManage.ME.put(TableName(), "Object"+id, object);
            return object;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Save method
     * @return
     */
    public long Save(){
        if (this.getId() > 0){
            this._InsertObject(this);
        }else {
            this.setId(this._InsertObject(this));
        }
        clearListCache();
        return this.getId();
    }

    /**
     * Update method
     * @return
     */
    public boolean Update() {
        Map<String, Object> map = ListInsertableFields();
        Object id = getId();
        Set<Map.Entry<String, Object>> entrys = map.entrySet();
        Object[] params = new Object[entrys.size()];
        StringBuilder sql = new StringBuilder("UPDATE ").append(TableName()).append(" SET ");
        int index = 0;
        for (Map.Entry<String, Object> entry : entrys) {
            sql.append("`"+entry.getKey()+"`").append("=?,");
            params[index] = entry.getValue();
            index++;
        }
        sql.replace(sql.length() - 1, sql.length(), " WHERE id=");
        sql.append(id);
        // remove old cache
        CacheManage.ME.del(TableName(), "Object"+id);
        clearListCache();
        return QueryHelper.update(sql.toString(), params) > 0;
    }

    /**
     * Delete method
     * @return
     */
    public boolean Delete(){
        if (getId()>0){
            String sql = "delete from "+TableName()+" where id=?";
            clearListCache();
            return QueryHelper.update(sql, getId()) > 0;
        }else {
            return false;
        }
    }

    public long getId() {
        return _key_id;
    }

    public void setId(long id){
        this._key_id = id;
    }

    private Map<String, Object> ListInsertableFields() {
        Map<String, Object> props = null;
        try {
            props = BeanUtils.describe(this);

            if (getId() <= 0) {
                props.remove("id");
            }

            props.remove("class");

            for (Map.Entry<String, Object> entry : props.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("_")){
                    props.remove(key);
                }
            }

            return props;
        } catch (Exception e) {
            throw new RuntimeException("Exception when Fetching fields of " + this);
        }
    }

    private long _InsertObject(Pojo obj) {
        Map<String, Object> pojo_bean = obj.ListInsertableFields();
        String[] fields = pojo_bean.keySet().toArray(
                new String[pojo_bean.size()]);
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(obj.TableName());
        sql.append("(`");
        for (int i = 0; i < fields.length; i++) {
            if (i > 0)
                sql.append("`,`");
            sql.append(fields[i]);
        }
        sql.append("`) VALUES(");
        for (int i = 0; i < fields.length; i++) {
            if (i > 0)
                sql.append(',');
            sql.append('?');
        }
        sql.append(')');
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = DBManager.ME.getConnection().prepareStatement(sql.toString(),
                    PreparedStatement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < fields.length; i++) {
                ps.setObject(i + 1, pojo_bean.get(fields[i]));
            }
            ps.executeUpdate();
            if (getId() > 0)
                return getId();

            rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getLong(1) : -1;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
            sql = null;
            fields = null;
            pojo_bean = null;
        }
        return 0;
    }

    /**
     * 清除列表缓存
     */
    private void clearListCache(){
        CacheManage.ME.clearList(TableName());
    }

    /**
     * Cached Query
     * @return
     */
    protected <T> List<T> Query(Class<T> beanClass, String sql, Object... params) throws SQLException {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        if (stack.length<2){
            return null;
        }
        String caller = stack[1].getMethodName();

        Object cachedList = CacheManage.ME.get("__List", this.serializeMethodName(stack[1], sql, params));
        if (cachedList != null) {
            return (List<T>) cachedList;
        }

        List<T> list = QueryHelper.query(beanClass, sql, params);

        this.tryCacheData(caller, sql, list, params);
        return list;
    }

    /**
     * 统计
     * @param sql
     * @param params
     * @return
     */
    public long Stat(String sql, Object... params) throws SQLException {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        if (stack.length<2){
            return 0;
        }
        String caller = stack[1].getMethodName();

        Object cachedCount = CacheManage.ME.get("__List", this.serializeMethodName(stack[1], sql, params));
        if (cachedCount != null) {
            return (Long) cachedCount;
        }

        long count = QueryHelper.stat(sql,params);

        this.tryCacheData(caller, sql, count, params);
        return count;
    }

    /**
     * load list
     * @param beanClass
     * @param ids
     * @param <T>
     * @return
     */
    public <T extends Pojo> List<T>LoadList(Class<T> beanClass, List<Long> ids) {
        List<T> list = new ArrayList<T>();
        T obj = null;
        try {
            obj = (T)beanClass.newInstance();
            for (int i = 0; i < ids.size(); i++) {
                long id = ids.get(i);
                T item = obj.Get(id);
                list.add(item);
            }
            return list;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * load list
     * @param beanClass
     * @param ids
     * @param <T>
     * @return
     */
    public <T extends Pojo> List<T>LoadList(Class<T> beanClass, Long[] ids) {
        List<T> list = new ArrayList<T>();
        T obj = null;
        try {
            obj = (T)beanClass.newInstance();
            for (int i = 0; i < ids.length; i++) {
                long id = ids[i];
                T item = obj.Get(id);
                list.add(item);
            }
            return list;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 尝试 cache -- 若有注解则cache
     * @param caller
     * @param sql
     * @param data
     * @param params
     */
    private void tryCacheData(String caller, String sql, Object data, Object... params){
        Class classObject = this.getClass();
        Method[] methods = classObject.getMethods();
        for (Method method : methods){
            if (method.getName().equals(caller)){
                CacheAnnotation.ListCache anno = method.getAnnotation(CacheAnnotation.ListCache.class);
                String key = this.serializeMethodName(method, sql, params);
                CacheManage.ME.put("__List", key, data);
                String[] tables = anno.tables();
                for (String table : tables){
                    if (table != TableName()){
                        CacheManage.ME.addList(table, key);
                    }
                }
                CacheManage.ME.addList(TableName(), key);
            }
        }
    }

    private String serializeMethodName(Method method, String sql, Object[] param){
        String paramStr = "";
        for (Object paramEle : param) {
            paramStr += paramEle.toString();
        }
        sql += paramStr;

        String methodName = this.TableName() +"-"+ method.getName();
        String extraKey = StringUtils.MD5(sql);
        return methodName+"::"+extraKey;
    }

    private String serializeMethodName(StackTraceElement stack, String sql, Object[] param){
        String paramStr = "";
        for (Object paramEle : param) {
            paramStr += paramEle.toString();
        }
        sql += paramStr;

        String methodName = this.TableName() +"-"+ stack.getMethodName();
        String extraKey = StringUtils.MD5(sql);
        return methodName+"::"+extraKey;
    }
}
