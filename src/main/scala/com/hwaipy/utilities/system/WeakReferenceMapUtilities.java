package com.hwaipy.utilities.system;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 *
 * @author Hwaipy
 */
public class WeakReferenceMapUtilities {

    private static final WeakHashMap<Object, Map<Object, Object>> weakMap = new WeakHashMap<>();

    public static void put(Object weakReference, Object key, Object value) {
        Map<Object, Object> map = weakMap.get(weakReference);
        if (map == null) {
            map = new HashMap<>();
            weakMap.put(weakReference, map);
        }
        map.put(key, value);
    }

    public static Object get(Object weakReference, Object key) {
        Map<Object, Object> map = weakMap.get(weakReference);
        return map == null ? null : map.get(key);
    }
}
