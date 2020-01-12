package education.bert;

import java.util.List;
import java.util.Map;

public interface Cache<K, V> {
    V put(K key, V value);

    V get(K key);

    V remove(K key);

    Map<K, V> getMap();

    List<K> getKeys();
}
