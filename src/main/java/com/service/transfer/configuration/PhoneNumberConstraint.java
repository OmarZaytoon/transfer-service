package com.service.transfer.configuration;

import com.service.transfer.annotation.ValidPhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class PhoneNumberConstraint implements ConstraintValidator<ValidPhoneNumber, String> {

    private static final String PLUS_PREFIX_REGEX = "\\+[0-9]{7,15}";
    private static final String DOUBLE_ZERO_PREFIX_REGEX = "00[0-9]{7,15}";
    private static final String SINGLE_ZERO_PREFIX_REGEX = "0[0-9]{6,14}";
    private static final String NO_PREFIX_REGEX = "[1-9][0-9]{6,14}";
    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(phoneNumber)) {
            return true;
        }
        if (phoneNumber.startsWith("+")) {
            return phoneNumber.matches(PLUS_PREFIX_REGEX);
        } else if (phoneNumber.startsWith("00")) {
            return phoneNumber.matches(DOUBLE_ZERO_PREFIX_REGEX);
        } else if (phoneNumber.startsWith("0")) {
            return phoneNumber.matches(SINGLE_ZERO_PREFIX_REGEX);
        } else {
            return phoneNumber.matches(NO_PREFIX_REGEX);
        }
    }
}