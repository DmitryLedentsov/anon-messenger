package com.dimka228.messenger.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PasswordConstraintsValidator.class)
public @interface Password {

  String message() default "Invalid password! shold contain only english letters and numbers";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
