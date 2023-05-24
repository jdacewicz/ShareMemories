package com.sharememories.sharememories.validation.annotations;

import com.sharememories.sharememories.validation.FileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {FileValidator.class})
public @interface ValidFile {

    String message() default "Only PNG, JPG or JPEG images are allowed.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
