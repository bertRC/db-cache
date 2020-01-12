package education.bert;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CacheBasicTest {
    private final Cache<Integer, String> cache = new CacheLruImpl<>(3);

    @Test
    public void shouldThrowIllegalArgumentExceptionTest() {
        assertThrows(IllegalArgumentException.class, () ->
                new CacheLruImpl<Integer, String>(CacheLruImpl.minimalCacheSize - 1)
        );
    }

    @Test
    public void shouldThrowNullPointerExceptionTest() {
        final Cache<Integer, String> defaultCache = new CacheLruImpl<>();
        assertThrows(NullPointerException.class, () -> defaultCache.put(null, "Some Value"));
        assertThrows(NullPointerException.class, () -> defaultCache.put(0, null));
        assertThrows(NullPointerException.class, () -> defaultCache.put(null, null));
        assertThrows(NullPointerException.class, () -> defaultCache.get(null));
        assertThrows(NullPointerException.class, () -> defaultCache.remove(null));
    }

    @Test
    public void simplePutTest() {
        Integer key = 0;
        String value = "Value";
        String previousValue = cache.put(key, value);
        Map<Integer, String> expectedMap = new HashMap<>();
        expectedMap.put(key, value);
        List<Integer> expectedList = Arrays.asList(key);

        assertNull(previousValue);
        assertEquals(expectedMap, cache.getMap());
        assertEquals(expectedList, cache.getKeys());
    }

    @Test
    public void putGetTest() {
        Integer key = 0;
        String value = "Value";
        cache.put(key, value);

        assertEquals(value, cache.get(key));
    }

    @Test
    public void updateValueTest() {
        Integer key = 0;
        String value = "Value";
        cache.put(key, value);
        String newValue = "New Value";
        String previousValue = cache.put(key, newValue);

        assertEquals(value, previousValue);
        assertEquals(newValue, cache.get(key));
    }

    @Test
    public void putWithKickOutTest() {
        Map<Integer, String> expectedMap = new HashMap<>();
        expectedMap.put(1, "Value1");
        expectedMap.put(2, "Value2");
        expectedMap.put(3, "Value3");
        List<Integer> expectedList = Arrays.asList(1, 2, 3);
        cache.put(0, "Value0");
        cache.put(1, "Value1");
        cache.put(2, "Value2");
        cache.put(3, "Value3");

        assertNull(cache.get(0));
        assertEquals(expectedMap, cache.getMap());
        assertEquals(expectedList, cache.getKeys());
    }

    @Test
    public void removeTest() {
        cache.put(0, "Value");
        cache.remove(0);

        assertNull(cache.get(0));
    }
}
