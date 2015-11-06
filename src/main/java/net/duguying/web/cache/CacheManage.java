package net.duguying.web.cache;

import net.duguying.community.bean.Users;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.CacheManagerBuilder;
import org.ehcache.config.CacheConfigurationBuilder;

/**
 * Created by duguying on 2015/11/5.
 */
public class CacheManage {
    public static CacheManage ME = new CacheManage();

    private CacheManager cacheManager = null;
    private Cache C = null;

    public CacheManage(){
        this.cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("preConfigured",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder().buildConfig(String.class, Object.class))
                .build(true);

        this.C = this.cacheManager.createCache("myCache",
                CacheConfigurationBuilder.newCacheConfigurationBuilder().buildConfig(String.class, Object.class));
    }

    private Object _get(String key){
        return this.C.get(key);
    }

    private void _put(String key, Object value){
        this.C.put(key,value);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        this.cacheManager.close();
    }

    public static void main(String[] arg){
        Users user = new Users();
        user.setUsername("lijun");
        CacheManage.ME._put("hello", user);
        Users u = (Users) CacheManage.ME._get("hello");
        System.out.println(u.getUsername());
    }
}
