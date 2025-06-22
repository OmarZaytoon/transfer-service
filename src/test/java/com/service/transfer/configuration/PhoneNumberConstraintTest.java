package com.service.transfer.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PhoneNumberConstraintTest {

    PhoneNumberConstraint constraint = new PhoneNumberConstraint();
    @Test
    void validPhoneNumbers() {
        assertTrue(constraint.isValid(null, null));
        assertTrue(constraint.isValid("", null));
        assertTrue(constraint.isValid("+1234567", null));
        assertTrue(constraint.isValid("+123456789012345", null));
        assertTrue(constraint.isValid("001234567", null));
        assertTrue(constraint.isValid("00123456789012345", null));
        assertTrue(constraint.isValid("0123456", null));
        assertTrue(constraint.isValid("012345678901234", null));
        assertTrue(constraint.isValid("1234567", null));
        assertTrue(constraint.isValid("123456789012345", null));
    }

    @Test
    void invalidPhoneNumbers() {
        assertFalse(constraint.isValid("+123456", null));
        assertFalse(constraint.isValid("00123456", null));
        assertFalse(constraint.isValid("012345", null));
        assertFalse(constraint.isValid("0123456789012345", null));
        assertFalse(constraint.isValid("+1234567890123456", null));
        assertFalse(constraint.isValid("001234567890123456", null));
        assertFalse(constraint.isValid("+123-4567", null));
        assertFalse(constraint.isValid("123 4567", null));
        assertFalse(constraint.isValid("abc1234567", null));
    }
}
