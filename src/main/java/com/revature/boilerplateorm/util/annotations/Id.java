package com.revature.boilerplateorm.util.annotations;

import java.lang.annotation.*;

/**
 * Annotation @Id annotated on fields which is used as the "Primary Key" of the table.
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
}
