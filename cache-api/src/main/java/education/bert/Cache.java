package education.bert;

/**
 * An interface that describes the basic methods for cache implementation.
 *
 * @param <K> the type of keys maintained by this cache. As a key it is supposed to use some type of queries (e.g.
 *            SQL-queries).
 * @param <V> the type of cached values.
 */
public interface Cache<K, V> {

    /**
     * Adds a value to the cache associated with a specific key.
     *
     * @param key   key with which the specified value is to be associated.
     * @param value value to be added to the cache.
     * @return the previous value associated with key, or {@code null} if there was no mapping for key.
     */
    V put(K key, V value);

    /**
     * Returns the value corresponding to the specified key.
     *
     * @param key the key whose associated value is to be returned.
     * @return the value corresponding to the specified key, or {@code null} if the cache contains no mapping for the
     * key.
     */
    V get(K key);

    /**
     * Removes the value corresponding to the specified key.
     *
     * @param key the key whose associated value is to be removed.
     * @return the previous value associated with key, or {@code null} if there was no mapping for key.
     */
    V remove(K key);
}
