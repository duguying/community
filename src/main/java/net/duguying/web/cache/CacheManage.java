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

    /**
     * 创建缓存域
     * @param region
     * @return
     */
    private Cache createCache(String region){
        Cache cache = this.cacheManager.createCache(region,
                CacheConfigurationBuilder.newCacheConfigurationBuilder().buildConfig(String.class, Object.class));
        this.CMap.put(region, cache);
        return cache;
    }

    /**
     * 获取值
     * @param region 区域
     * @param key
     * @return
     */
    public Object get(String region, String key){
        Cache cache = this.CMap.get(region);
        if (cache == null){
            return null;
        }
        return cache.get(key);
    }

    /**
     * 存储键值
     * @param region 区域
     * @param key
     * @param value
     */
    public void put(String region, String key, Object value){
        Cache cache = this.CMap.get(region);
        if (cache == null){
            cache = this.createCache(region);
        }
        cache.put(key,value);
    }

    /**
     * 删除指定键值
     * @param region
     * @param key
     */
    public void del(String region, String key){
        Cache cache = this.CMap.get(region);
        if (cache == null){
            return;
        }
        cache.remove(key);
    }

    /**
     * 清理掉整个region
     * @param region
     */
    public void clearRegion(String region){
        Cache cache = this.CMap.get(region);
        cache.clear();
        this.CMap.remove(region);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        this.cacheManager.close();
    }

    public static void main(String[] arg){
        Users user = new Users();
        user.setUsername("lijun");
        CacheManage.ME.put("MyCache", "hello", user);
        Users u = (Users) CacheManage.ME.get("MyCache", "hello");
        System.out.println(u.getUsername());
    }
}
