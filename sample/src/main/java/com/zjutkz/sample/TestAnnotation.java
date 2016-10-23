package com.zjutkz.sample;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Created by kangzhe on 16/10/22.
 */
@Retention(CLASS)
@Target({METHOD, PARAMETER, FIELD})
public @interface TestAnnotation {

    int value();
}
