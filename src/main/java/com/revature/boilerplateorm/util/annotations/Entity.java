package com.revature.boilerplateorm.util.annotations;

import java.lang.annotation.*;

/**
 * Annotation @Entity is annotated on classes to let us know that it is a table and should be persisted.
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {

}
