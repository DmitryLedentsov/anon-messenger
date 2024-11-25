package com.dimka228.messenger.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import org.passay.*;

public class PasswordConstraintsValidator implements ConstraintValidator<Password, String> {

  @Override
  public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {

    PasswordValidator passwordValidator =
        new PasswordValidator(
            Arrays.asList(
                // Length rule. Min 10 max 128 characters
                new LengthRule(5, 128),

                // At least one upper case letter
                // new CharacterRule(EnglishCharacterData.UpperCase, 1),

                // At least one lower case letter
                new CharacterRule(EnglishCharacterData.LowerCase, 1),

                // At least one number
                // new CharacterRule(EnglishCharacterData.Digit, 1),

                // At least one special characters
                // new CharacterRule(EnglishCharacterData.Special, 1),

                new WhitespaceRule()));

    RuleResult result = passwordValidator.validate(new PasswordData(password));

    if (result.isValid()) {

      return true;
    }

    // Sending one message each time failed validation.
    constraintValidatorContext
        .buildConstraintViolationWithTemplate(
            passwordValidator.getMessages(result).stream().findFirst().get())
        .addConstraintViolation()
        .disableDefaultConstraintViolation();

    return false;
  }
}
