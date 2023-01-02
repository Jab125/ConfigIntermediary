package com.jab125.configintermediary.util;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static <T> T get(Object object, Field field) {
        try {
            return (T) field.get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void set(Object object, Object newValue, Field field) {
        try {
            field.set(object, newValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Field get(Class<?> clazz, String name) {
        try {
            return clazz.getField(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
