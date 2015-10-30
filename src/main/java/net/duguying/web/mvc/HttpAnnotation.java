package net.duguying.web.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by duguying on 2015/10/30.
 */
public @interface HttpAnnotation {
    /**
     * Post Method
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface URLMapping {
        public String uri();
        public String method() default "all";
    }
}
