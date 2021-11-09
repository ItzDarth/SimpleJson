package eu.simplejson.helper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface WrapperClass {

    /**
     * The class of the interface
     */
    Class<?> interfaceClass() default Class.class;

    /**
     * The wrapper class for the interface
     */
    Class<?> value();
}
