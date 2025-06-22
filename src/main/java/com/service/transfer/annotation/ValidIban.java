package com.service.transfer.annotation;

import com.service.transfer.configuration.IbanValidatorConstraint;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IbanValidatorConstraint.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIban {
    String message() default "Invalid IBAN";
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
