package org.lboot.mrest.annotation;

import java.lang.annotation.*;

/**
 * 信道ID
 */
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SseSocketId {
    String value() default "";
}
