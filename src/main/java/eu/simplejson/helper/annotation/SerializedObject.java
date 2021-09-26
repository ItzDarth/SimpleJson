package eu.simplejson.helper.annotation;

import eu.simplejson.helper.exlude.ExcludeStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SerializedObject {

    /**
     * The amount of times a field of an object
     * will be serialized if its the same type as the class
     * (to prevent StackOverFlow)
     */
    int serializeSameFieldInstance() default -1;

    /**
     * The fields that will be ignored
     * if the field type matches any of the classe´s
     */
    Class<?>[] excludeClasses() default {};

    /**
     * A custom {@link ExcludeStrategy} for this object
     * Warning!
     * It will clear all other strategies and only uses
     * this provided {@link ExcludeStrategy}
     */
    Class<? extends ExcludeStrategy> strategy() default ExcludeStrategy.class;
}
