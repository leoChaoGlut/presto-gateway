package personal.leo.presto.gateway.cache;

import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.SimpleCacheResolver;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
public class CaffeineCacheResolver extends SimpleCacheResolver {

    public CaffeineCacheResolver(CacheManager cacheManager) {
        super(cacheManager);
    }

    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        Collection<String> cacheNames = getCacheNames(context);
        if (CollectionUtils.isEmpty(cacheNames)) {
            return Collections.emptyList();
        }
        final CaffeineCacheManager caffeineCacheManager = (CaffeineCacheManager) getCacheManager();
        final Set<String> oldCacheNames = new HashSet<>(caffeineCacheManager.getCacheNames());
        final List<String> newCacheNames = cacheNames.stream()
                .filter(cacheName -> !oldCacheNames.contains(cacheName))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(newCacheNames)) {
            caffeineCacheManager.setCacheNames(newCacheNames);
        }

        return cacheNames.stream()
                .map(caffeineCacheManager::getCache)
                .collect(Collectors.toList());
    }
}
