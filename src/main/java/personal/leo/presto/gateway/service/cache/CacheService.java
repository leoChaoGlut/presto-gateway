package personal.leo.presto.gateway.service.cache;

public interface CacheService {

    boolean isCacheEnabled();

    void disableCache();

    void enableCache();

    void put(Object key, Object value);

    <T> T get(Object key);

}
