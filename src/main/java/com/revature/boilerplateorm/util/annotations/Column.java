package com.revature.boilerplateorm.util.annotations;


import java.lang.annotation.*;


/**
 * Annotation @Column only used to explicitly state the column in which the data should be listed to.
 * By default, the class name will implicitly determine the column it is linked to.
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String name();
}
