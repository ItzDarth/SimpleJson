package eu.simplejson.helper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SerializedField {

    /**
     * The serialized name of this field
     */
    String name() default "";

    /**
     * If this field should be skipped
     */
    boolean ignore() default false;

    /**
     * All {@link WrapperClass}es for this field to replace
     */
    WrapperClass[] wrapperClasses() default {};
}
