package cache;

import java.io.IOException;

import main.Variables;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

public class LocalMemcachedCache implements ICache {

    public static final MemcachedClient CLIENT;
    static {
        try {
            MemcachedClientBuilder builder =
                new XMemcachedClientBuilder(
                    AddrUtil.getAddresses("localhost:11211"));
            builder.setConnectionPoolSize(Variables.THREADS);
            CLIENT = builder.build();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void reset() throws Exception {
        CLIENT.flushAll();
    }

    @Override
    public String get(String key) throws Exception {
        return CLIENT.get(key);
    }

    @Override
    public void set(String key, String value) throws Exception {
        CLIENT.set(key, 24 * 60 * 60, value);
    }
}
