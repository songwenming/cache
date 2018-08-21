package com.pk.cache.caffeine.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by pangkunkun on 2018/8/17.
 */
@Service
public class CaffeineService {
    private static final Logger logger = LoggerFactory.getLogger(CaffeineService.class);

    @Autowired
    private CacheManager cacheManager;

    @Resource(name = "refreshAfterWrite")
    private com.github.benmanes.caffeine.cache.Cache cache;

    public CacheStats stats(){
        CacheStats stats = cache.stats();
        //cleanUp()回收失效的数据
        cache.cleanUp();
        return null;
    }

    public void saveCache(String key){
        cache.put(key, key);
    }

    public Object getCache(String key){
        logger.info("size = {}",cache.estimatedSize());
        Object value = cache.getIfPresent(key);
//        LoadingCache loadingCache =  (LoadingCache) cache;
//        CacheStats stats = loadingCache.stats();
//        loadingCache.refresh(key);
        logger.info("value = {}",value);
        return value;
    }

    public Object getCacheService(String key){
        return key + " nihao ";
    }

    /**
     * 如果cacheLoad生效这里的方法主体不会被执行
     * */
    @Cacheable(cacheNames = "outLimit",key = "#name",cacheResolver = "simpleCacheResolver")
    public String addCaffeineServiceTest(String name){
        String value = name + " nihao";
        logger.info("addCaffeineServiceTest value = {}",value);

        return value;
    }


    /**
     * condition条件判断是否要走缓存，无法使用方法中出现的值（返回结果等）,条件为true放入缓存
     * unless是方法执行后生效，决定是否放入缓存,返回true的放缓存，与condition相反
     * */
    @Cacheable(cacheNames = "outLimit",key = "#name",unless = "#value != null ")
    public String getCaffeineServiceTest(String name,Integer age){
        String value = name + " nihao "+ age;
        logger.info("getCaffeineServiceTest value = {}",value);
        return value;
    }

    /**
     * CachePut修改key的value，会调用cache的put
     * */
    @CachePut(cacheNames = "outLimit",cacheResolver = "namedCacheResolver")
    public String updateCaffeineServiceTest(String name){
        String value = name + " nihao";
        logger.info("updateCaffeineServiceTest value = {}",value);
        return null;
    }

    /**
     * CacheEvict删除key，会调用cache的evict
     * */
    @CacheEvict(cacheNames = "outLimit",key = "#name")
    public String deleteCaffeineServiceTest(String name){
        String value = name + " nihao";
        logger.info("deleteCaffeineServiceTest value = {}",value);
        return value;
    }

    public void testCacheManager(){
        Cache cache = cacheManager.getCache("outLimit");
        logger.info("cache = {}",cache.getNativeCache());
    }

    private void cacheResolver(){
        logger.info("This is cacheResolver");
    }
}
