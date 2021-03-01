package io.vson.annotation.other;

import io.vson.elements.object.VsonMember;
import io.vson.enums.VsonSettings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

@Target({
				ElementType.METHOD,
				ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface VsonConfiguration {

	String file();

	VsonSettings[] vsonSettings() default {};

}
