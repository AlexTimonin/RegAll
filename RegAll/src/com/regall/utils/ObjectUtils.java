package com.regall.utils;

import java.util.Map;

/**
 * Created by Alex on 09.02.2015.
 */
public class ObjectUtils {

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

}
