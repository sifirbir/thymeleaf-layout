package org.sifirbir.spring.thymeleaf.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Layout(DynamicLayout.NAME)
public @interface DynamicLayout {
	public static final String NAME = "LAYOUT_DYNAMIC";
	
	String key() default "layout";
}
