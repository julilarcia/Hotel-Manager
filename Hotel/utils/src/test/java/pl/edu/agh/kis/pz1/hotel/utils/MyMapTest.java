package pl.edu.agh.kis.pz1.hotel.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyMapTest {

    private Map<String, Integer> map;

    @BeforeEach
    void setUp() {
        map = new MyMap<>();
    }

    @Test
    void testPutAndGet() {
        assertTrue(map.put("a", 1));
        assertEquals(1, map.get("a"));
    }

    @Test
    void testPutOverwrite() {
        map.put("a", 1);
        assertTrue(map.put("a", 2));
        assertEquals(2, map.get("a"));
    }

    @Test
    void testPutNullKeyOrValue() {
        assertFalse(map.put(null, 1));
        assertFalse(map.put("a", null));
        assertNull(map.get(null));
    }

    @Test
    void testRemoveExistingKey() {
        map.put("a", 1);
        assertTrue(map.remove("a"));
        assertNull(map.get("a"));
    }

    @Test
    void testRemoveNonExistingKey() {
        assertFalse(map.remove("x"));
    }

    @Test
    void testContainsKey() {
        map.put("a", 1);
        assertTrue(map.contains("a"));
        assertFalse(map.contains("x"));
    }

    @Test
    void testKeysReturnsCopy() {
        map.put("a", 1);
        map.put("b", 2);
        List<String> keys = map.keys();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("a"));
        assertTrue(keys.contains("b"));

        keys.remove("a");
        assertTrue(map.contains("a"));
    }

    @Test
    void testGetNonExistingKey() {
        assertNull(map.get("x"));
    }

    @Test
    void testEmptyMap() {
        assertTrue(map.keys().isEmpty());
        assertFalse(map.contains("anything"));
        assertNull(map.get("anything"));
    }
}
