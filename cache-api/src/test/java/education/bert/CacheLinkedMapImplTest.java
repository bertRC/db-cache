package education.bert;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CacheLinkedMapImplTest {
    private final Cache<Integer, String> cache = new CacheLinkedMapImpl<>(3);

    @Test
    public void shouldThrowIllegalArgumentExceptionTest() {
        assertDoesNotThrow(() -> new CacheLinkedMapImpl<Integer, String>());
        assertThrows(IllegalArgumentException.class, () -> new CacheLinkedMapImpl<Integer, String>(0));
    }

    @Test
    public void putGetTest() {
        Integer key = 0;
        String value = "Value";
        assertNull(cache.put(key, value));
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
        cache.put(0, "Value0");
        cache.put(1, "Value1");
        cache.put(2, "Value2");
        cache.put(3, "Value3");

        assertEquals("Value1", cache.get(1));
        assertEquals("Value2", cache.get(2));
        assertEquals("Value3", cache.get(3));
        assertNull(cache.get(0));
    }

    @Test
    public void removeTest() {
        cache.put(0, "Value");
        assertEquals("Value", cache.remove(0));
        assertNull(cache.get(0));
    }

    @Test
    public void removeNotExistedTest() {
        assertNull(cache.get(0));
        assertNull(cache.remove(0));
    }
}
