package com.hr.admin.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    int value() default 60;
    int timeWindow() default 60;
    String key() default "";
}
