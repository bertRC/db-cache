package education.bert;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cache implementation based on Least recently used (LRU) cache replacement policy using LinkedHashMap.
 *
 * @param <K> the type of keys maintained by this cache. As a key it is supposed to use some type of queries (e.g.
 *            SQL-queries).
 * @param <V> the type of cached values.
 */
public class CacheLinkedMapImpl<K, V> implements Cache<K, V> {

    /**
     * The minimum number of items to store.
     */
    public static final int minimalCacheSize = 1;

    /**
     * The default maximum number of items to store specified by default constructor.
     */
    public static final int defaultCacheSize = 10;

    /**
     * A map for storing items.
     */
    private final LinkedHashMap<K, V> cacheMap;

    /**
     * Constructs a cache class with cacheMap configured for LRU replacement policy.
     *
     * @param maxCacheSize the maximum cacheMap size.
     */
    public CacheLinkedMapImpl(int maxCacheSize) {
        if (maxCacheSize < minimalCacheSize) {
            throw new IllegalArgumentException("maxCacheSize must not be less than minimalCacheSize (" + minimalCacheSize + ")");
        }

        cacheMap = new LinkedHashMap<K, V>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxCacheSize;
            }
        };
    }

    /**
     * Constructs a cache class and sets the default value for maximum cacheMap size.
     */
    public CacheLinkedMapImpl() {
        this(defaultCacheSize);
    }

    /**
     * Adds a value to the cache associated with a specific key. If the cache previously contained a mapping for the
     * key, the old value is replaced. When the cache size exceeds its maximum, the least recently accessed element will
     * be deleted by LinkedHashMap.
     *
     * @param key   key with which the specified value is to be associated.
     * @param value value to be added to the cache.
     * @return the previous value associated with key, or {@code null} if there was no mapping for key.
     */
    @Override
    public synchronized V put(K key, V value) {
        return cacheMap.put(key, value);
    }

    /**
     * Returns the value corresponding to the specified key.
     *
     * @param key the key whose associated value is to be returned.
     * @return the value corresponding to the specified key, or {@code null} if the cache contains no mapping for the
     * key.
     */
    @Override
    public V get(K key) {
        return cacheMap.get(key);
    }

    /**
     * Removes the value corresponding to the specified key.
     *
     * @param key the key whose associated value is to be removed.
     * @return the previous value associated with key, or {@code null} if there was no mapping for key.
     */
    @Override
    public synchronized V remove(K key) {
        return cacheMap.remove(key);
    }
}
