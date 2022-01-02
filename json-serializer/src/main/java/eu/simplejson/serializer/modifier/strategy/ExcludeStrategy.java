package eu.simplejson.serializer.modifier.strategy;

import java.lang.reflect.Field;

public interface ExcludeStrategy {

    boolean shouldSkipField(Field field);

    boolean shouldSkipClass(Class<?> cls);
}
