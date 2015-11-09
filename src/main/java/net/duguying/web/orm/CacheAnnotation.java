package net.duguying.web.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by duguying on 2015/11/9.
 */
public @interface CacheAnnotation {
    /**
     * Post Method
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface ListCache {
        public String[] tables() default {};
    }
}
