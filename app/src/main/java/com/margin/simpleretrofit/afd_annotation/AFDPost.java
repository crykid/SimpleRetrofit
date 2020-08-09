package com.margin.afd_annotation.customer_retrofit.afd_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by  : blank
 * Create one : 2020/8/9 at 19:14
 * Description :
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AFDPost {
    String value();
}
