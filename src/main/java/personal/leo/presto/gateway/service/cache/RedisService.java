package personal.leo.presto.gateway.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Auhtor chenliang
 * @Date 2020/8/28
 **/
@Service
public class RedisService implements CacheService {

    @Autowired
    private RedisTemplate redisTemplate;

    private static AtomicBoolean cacheEnabled = new AtomicBoolean(false);

    @Override
    public boolean isCacheEnabled() {
        return cacheEnabled.get();
    }

    @Override
    public void disableCache() {
        cacheEnabled.set(false);
    }

    @Override
    public void enableCache() {
        cacheEnabled.set(true);
    }

    @Override
    public void put(Object key, Object value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(10));
    }

    @Override
    public <T> T get(Object key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

}
