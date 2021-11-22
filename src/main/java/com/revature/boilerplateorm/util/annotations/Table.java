package com.revature.boilerplateorm.util.annotations;

import java.lang.annotation.*;

/**
 * Annotation @Table annotated on classes to explicitly tell what data table is linked to this class.
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String name();
}
