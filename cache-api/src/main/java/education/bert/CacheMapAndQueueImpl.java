package education.bert;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Cache implementation based on First in first out (FIFO) cache replacement policy. This is the simplest implementation
 * using a HashMap and LinkedList collections.
 *
 * @param <K> the type of keys maintained by this cache. As a key it is supposed to use some type of queries (e.g.
 *            SQL-queries).
 * @param <V> the type of cached values.
 */
public class CacheMapAndQueueImpl<K, V> implements Cache<K, V> {

    /**
     * The maximum number of items to store specified by constructor.
     */
    private int maxCacheSize;

    /**
     * The minimum number of items to store.
     */
    public static final int minimalCacheSize = 1;

    /**
     * The default maximum number of items to store specified by default constructor.
     */
    public static final int defaultCacheSize = 10;

    /**
     * General collection for storing items providing fast access.
     */
    private final HashMap<K, V> cacheMap;

    /**
     * LinkedList that allows FIFO cache replacement policy.
     */
    private final LinkedList<K> cacheQueue;

    /**
     * Constructs a cache class with empty cacheMap and cacheQueue collections and sets the maxCacheSize.
     *
     * @param maxCacheSize the maximum number of items to store.
     */
    public CacheMapAndQueueImpl(int maxCacheSize) {
        if (maxCacheSize < minimalCacheSize) {
            throw new IllegalArgumentException("maxCacheSize must not be less than minimalCacheSize (" + minimalCacheSize + ")");
        }
        this.maxCacheSize = maxCacheSize;

        cacheMap = new HashMap<>();
        cacheQueue = new LinkedList<>();
    }

    /**
     * Constructs a cache class and sets the default value for maxCacheSize.
     */
    public CacheMapAndQueueImpl() {
        this(defaultCacheSize);
    }

    /**
     * Adds a value to the cache associated with a specific key. If the cache previously contained a mapping for the
     * key, the old value is replaced. If the cache size reaches its maximum, the kickOut() method is invoked to remove
     * the first item from the cache.
     *
     * @param key   key with which the specified value is to be associated.
     * @param value value to be added to the cache.
     * @return the previous value associated with key, or {@code null} if there was no mapping for key.
     */
    @Override
    public synchronized V put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException();
        }
        if (cacheMap.containsKey(key)) {
            return cacheMap.put(key, value);
        }
        if (cacheMap.size() >= maxCacheSize) {
            kickOut();
        }
        cacheQueue.offer(key);
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
        if (key == null) {
            throw new NullPointerException();
        }
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
        if (key == null) {
            throw new NullPointerException();
        }
        cacheQueue.remove(key);
        return cacheMap.remove(key);
    }

    /**
     * Removes the first key from the cacheQueue and its corresponding value from the cacheMap. This method will be
     * invoked when the number of items in the cache reaches its maximum (FIFO cache replacement policy).
     */
    private void kickOut() {
        cacheMap.remove(Objects.requireNonNull(cacheQueue.poll()));
    }
}
