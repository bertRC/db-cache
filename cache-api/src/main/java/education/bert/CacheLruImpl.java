package education.bert;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CacheLruImpl<K, V> implements Cache<K, V> {
    private int cacheSize;
    public static final int minimalCacheSize = 1;
    public static final int defaultCacheSize = 10;

    private final ConcurrentHashMap<K, V> cacheMap;
    private final ConcurrentLinkedQueue<K> cacheQueue;

    public CacheLruImpl(int cacheSize) {
        if (cacheSize < minimalCacheSize) {
            throw new IllegalArgumentException("cacheSize must not be less than minimalCacheSize (" + minimalCacheSize + ")");
        }
        this.cacheSize = cacheSize;

        cacheMap = new ConcurrentHashMap<>();
        cacheQueue = new ConcurrentLinkedQueue<>();
    }

    public CacheLruImpl() {
        this(defaultCacheSize);
    }

    @Override
    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException();
        }
        if (cacheMap.containsKey(key)) {
            return cacheMap.put(key, value);
        }
        if (cacheMap.size() >= cacheSize) {
            kickOut();
        }
        cacheQueue.offer(key);
        return cacheMap.put(key, value);
    }

    @Override
    public V get(K key) {
        if (key == null) {
            throw new NullPointerException();
        }
        return cacheMap.get(key);
    }

    @Override
    public V remove(K key) {
        if (key == null) {
            throw new NullPointerException();
        }
        cacheQueue.remove(key);
        return cacheMap.remove(key);
    }

    private void kickOut() {
        K key = cacheQueue.poll();
        if (key != null) {
            cacheMap.remove(key);
        }
    }
}
