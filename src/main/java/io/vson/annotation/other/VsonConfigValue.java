package io.vson.annotation.other;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface VsonConfigValue {

	String key() default "no_key";

	String file();

	String[] subVsons() default {};

	Class<?> treeClass() default VsonConfigValue.class;

	char colorChar() default '&';

}
