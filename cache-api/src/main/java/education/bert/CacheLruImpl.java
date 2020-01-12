package education.bert;

import java.util.*;

public class CacheLruImpl<K, V> implements Cache<K, V> {
    private int maxCacheSize;
    public static final int minimalCacheSize = 1;
    public static final int defaultCacheSize = 10;

    private final HashMap<K, V> cacheMap;
    private final LinkedList<K> cacheQueue;

    public CacheLruImpl(int maxCacheSize) {
        if (maxCacheSize < minimalCacheSize) {
            throw new IllegalArgumentException("maxCacheSize must not be less than minimalCacheSize (" + minimalCacheSize + ")");
        }
        this.maxCacheSize = maxCacheSize;

        cacheMap = new HashMap<>();
        cacheQueue = new LinkedList<>();
    }

    public CacheLruImpl() {
        this(defaultCacheSize);
    }

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

    @Override
    public V get(K key) {
        if (key == null) {
            throw new NullPointerException();
        }
        return cacheMap.get(key);
    }

    @Override
    public synchronized V remove(K key) {
        if (key == null) {
            throw new NullPointerException();
        }
        cacheQueue.remove(key);
        return cacheMap.remove(key);
    }

    @Override
    public Map<K, V> getMap() {
        return new HashMap<>(cacheMap);
    }

    @Override
    public List<K> getKeys() {
        return new ArrayList<>(cacheQueue);
    }

    private void kickOut() {
        cacheMap.remove(Objects.requireNonNull(cacheQueue.poll()));
    }
}
