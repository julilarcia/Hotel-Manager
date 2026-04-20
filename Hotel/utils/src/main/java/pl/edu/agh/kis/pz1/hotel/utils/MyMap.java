package pl.edu.agh.kis.pz1.hotel.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple generic Map-like implementation backed by two parallel lists: one for keys and one for values.
 *
 * <p>This class is intentionally minimal and serves the exercise purpose of implementing a map
 * abstraction using basic collections. Keys and values must be non-null. Equality for keys is
 * determined by {@link Object#equals(Object)} via {@link List#indexOf(Object)}.</p>
 *
 * @param <K> key type
 * @param <V> value type
 */
public class MyMap<K, V> implements Map<K, V> {
    private final List<K> keys = new ArrayList<>();
    private final List<V> values = new ArrayList<>();

    /**
     * Associates the specified value with the specified key in this map.
     *
     * <p>If the map previously contained a mapping for the key, the old value is replaced.
     * Returns {@code false} and performs no operation when either {@code key} or {@code value}
     * is {@code null}.</p>
     *
     * @param key   key with which the specified value is to be associated; must not be null
     * @param value value to be associated with the specified key; must not be null
     * @return {@code true} when the operation succeeded, {@code false} for null key/value
     */
    @Override
    public boolean put(K key, V value) {
        if (key == null || value == null) {
            return false;
        }
        int index = keys.indexOf(key);
        if (index != -1) {
            values.set(index, value);
        } else {
            keys.add(key);
            values.add(value);
        }
        return true;
    }

    /**
     * Removes the mapping for a key from this map if it is present.
     *
     * @param key key whose mapping is to be removed
     * @return {@code true} if the mapping was present and removed, {@code false} otherwise
     */
    @Override
    public boolean remove(K key) {
        int index = keys.indexOf(key);
        if (index != -1) {
            keys.remove(index);
            values.remove(index);
            return true;
        }
        return false;
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value associated with {@code key}, or {@code null} if none
     */
    @Override
    public V get(K key) {
        int index = keys.indexOf(key);
        return index != -1 ? values.get(index) : null;
    }

    /**
     * Returns a list of keys contained in this map.
     *
     * <p>The returned list is a shallow copy; modifications to it do not affect the map.</p>
     *
     * @return list of keys currently stored in the map
     */
    @Override
    public List<K> keys() {
        return new ArrayList<>(keys);
    }

    /**
     * Checks whether the map contains a mapping for the specified key.
     *
     * @param key key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the key, {@code false} otherwise
     */
    @Override
    public boolean contains(K key) {
        return keys.contains(key);
    }
}


