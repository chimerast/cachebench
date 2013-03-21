package cache;

import java.util.concurrent.TimeUnit;

import main.Variables;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class HashMapCache implements ICache {

    private static final Cache<String, String> CACHE = CacheBuilder
        .newBuilder()
        .expireAfterAccess(1, TimeUnit.DAYS)
        .concurrencyLevel(Variables.THREADS)
        .maximumSize(10000)
        .build();

    @Override
    public void reset() throws Exception {
        CACHE.invalidateAll();
    }

    @Override
    public String get(String key) throws Exception {
        return CACHE.getIfPresent(key);
    }

    @Override
    public void set(String key, String value) throws Exception {
        CACHE.put(key, value);
    }
}
