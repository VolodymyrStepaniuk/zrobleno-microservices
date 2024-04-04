package com.stepaniuk.zrobleno.validation.service;

import jakarta.validation.Constraint;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import java.lang.annotation.*;

@Target({ElementType.TYPE_USE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
@Length(min = 1, max = 2048)
@URL
public @interface ImageUrl {

}
