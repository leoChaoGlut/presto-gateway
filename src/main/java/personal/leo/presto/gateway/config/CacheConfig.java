package personal.leo.presto.gateway.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import personal.leo.presto.gateway.cache.CaffeineCacheResolver;
import personal.leo.presto.gateway.constants.CacheResolverNames;

import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean(CacheResolverNames._10minCacheResolver)
    public CacheResolver _10minCacheResolver() {
        final CaffeineCacheManager _10minCacheManager = new CaffeineCacheManager();
        final Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .maximumSize(5_0000L)
                .softValues()
                .expireAfterWrite(3L, TimeUnit.MINUTES);
        _10minCacheManager.setCaffeine(caffeine);

        return new CaffeineCacheResolver(_10minCacheManager);
    }

}
