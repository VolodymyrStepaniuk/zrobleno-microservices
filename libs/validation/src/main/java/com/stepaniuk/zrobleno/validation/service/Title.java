package com.stepaniuk.zrobleno.validation.service;

import jakarta.validation.Constraint;
import org.hibernate.validator.constraints.Length;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
@Length(min = 1, max = 255)
public @interface Title {

}
