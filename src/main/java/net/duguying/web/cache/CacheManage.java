package net.duguying.web.cache;

import net.duguying.community.bean.Users;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.CacheManagerBuilder;
import org.ehcache.config.CacheConfigurationBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by duguying on 2015/11/5.
 */
public class CacheManage {
    public static CacheManage ME = new CacheManage();

    private CacheManager cacheManager = null;
    private Map<String, Cache> CMap = new HashMap<String, Cache>();

    public CacheManage(){
        this.cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("preConfigured",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder().buildConfig(String.class, Object.class))
                .build(true);

    }

    public void createCache(String region){
        Cache cache = this.cacheManager.createCache(region,
                CacheConfigurationBuilder.newCacheConfigurationBuilder().buildConfig(String.class, Object.class));
        this.CMap.put(region, cache);
    }

    public Cache getCache(String region){
        return this.CMap.get(region);
    }

    private Object _get(String region, String key){
        Cache cache = this.CMap.get(region);
        if (cache == null){
            return null;
        }
        return cache.get(key);
    }

    private void _put(String region, String key, Object value){
        Cache cache = this.CMap.get(region);
        cache.put(key,value);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        this.cacheManager.close();
    }

    public static void main(String[] arg){
        Users user = new Users();
        user.setUsername("lijun");
        CacheManage.ME.createCache("MyCache");
        CacheManage.ME._put("MyCache", "hello", user);
        Users u = (Users) CacheManage.ME._get("MyCache", "hello");
        System.out.println(u.getUsername());
    }
}
