package net.duguying.web.orm;

import net.duguying.web.tool.StringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.dbutils.DbUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by duguying on 2015/10/30.
 */
public class Pojo {
    private int id;
    private long _key_id;

    public String TableName(){
        return StringUtils.camelToUnderline(this.getClass().getSimpleName());
    }

    public <T extends Pojo> T Get(long id){
        String sql = "select * from "+TableName()+" where id=?";
        try {
            return (T)QueryHelper.read(this.getClass(),sql,id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long Save(){
        if (this.getId() > 0){
            this._InsertObject(this);
        }else {
            this.setId(this._InsertObject(this));
        }
        return this.getId();
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
}
