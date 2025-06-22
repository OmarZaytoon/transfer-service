package com.service.transfer.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IbanValidatorConstraintTest {
    private IbanValidatorConstraint validator;

    @BeforeEach
    public void setup() {
        validator = new IbanValidatorConstraint();
    }
    @Test
    public void testValidIbanWithSpaces() {
        assertTrue(validator.isValid("GB82 WEST 1234 5698 7654 32", null));
        assertTrue(validator.isValid("DE89 3704 0044 0532 0130 00", null));
        assertTrue(validator.isValid("FR14 2004 1010 0505 0001 3M02 606", null));
    }
    @Test
    public void testValidIbanWithoutSpaces() {
        assertTrue(validator.isValid("GB82WEST12345698765432", null));
        assertTrue(validator.isValid("DE89370400440532013000", null));
    }

    @Test
    public void testInvalidIbanWrongChecksum() {
        assertFalse(validator.isValid("GB82 WEST 1234 5698 7654 33", null));
        assertFalse(validator.isValid("DE89 3704 0044 0532 0130 01", null));
    }

    @Test
    public void testNullIban() {
        assertFalse(validator.isValid(null, null));
    }

    @Test
    public void testTooShortIban() {
        assertFalse(validator.isValid("GB8", null));
        assertFalse(validator.isValid("", null));
    }

    @Test
    public void testLowercaseIban() {
        assertTrue(validator.isValid("gb82 west 1234 5698 7654 32", null));
    }

    @Test
    public void testIbanWithExtraSpaces() {
        assertTrue(validator.isValid("  GB82  WEST  1234  5698 7654  32  ", null));
    }

    @Test
    public void testIbanWithInvalidCharacters() {
        assertFalse(validator.isValid("GB82 WEST 1234 56#8 7654 32", null));
        assertFalse(validator.isValid("GB82@WEST12345698765432", null));
    }

    @Test
    public void testIbanWithNonAlphanumericCausingException() {
        assertFalse(validator.isValid("GB82 WEST 1234 56$8 7654 32", null));
    }

}
