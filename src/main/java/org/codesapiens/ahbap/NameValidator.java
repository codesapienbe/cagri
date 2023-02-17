package org.codesapiens.ahbap;

import org.codesapiens.ahbap.data.Name;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<Name, String> {

    private static final String
            INTERNATIONAL_NAME_REGEX = "^([a-zA-Z]{2,}\\s[a-zA-Z]+'?-?[a-zA-Z]{2,}\\s?([a-zA-Z]+)?)";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.matches(INTERNATIONAL_NAME_REGEX) && value.length() > 1;
    }
}