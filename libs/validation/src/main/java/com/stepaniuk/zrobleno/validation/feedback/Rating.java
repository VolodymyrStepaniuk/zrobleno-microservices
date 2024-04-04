package com.stepaniuk.zrobleno.validation.feedback;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
@Max(5)
@Min(1)
public @interface Rating {

}
