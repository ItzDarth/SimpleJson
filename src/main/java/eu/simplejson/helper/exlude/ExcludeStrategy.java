package eu.simplejson.helper.exlude;

import java.lang.reflect.Field;

public interface ExcludeStrategy {

    boolean shouldSkipField(Field field);

    boolean shouldSkipClass(Class<?> cls);
}
