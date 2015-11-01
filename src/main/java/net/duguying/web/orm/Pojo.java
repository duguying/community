package net.duguying.web.orm;

import net.duguying.web.tool.StringUtils;

/**
 * Created by duguying on 2015/10/30.
 */
public class Pojo {
    public String TableName(){
        return StringUtils.camelToUnderline(this.getClass().getSimpleName());
    }

}
