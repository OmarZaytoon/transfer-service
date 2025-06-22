package com.service.transfer.configuration;

import com.service.transfer.annotation.ValidIban;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigInteger;


public class IbanValidatorConstraint  implements ConstraintValidator<ValidIban, String> {

    @Override
    public boolean isValid(String iban, ConstraintValidatorContext context) {
        if (iban == null || iban.length() < 5) return false;
        iban = iban.replaceAll("\\s+", "").toUpperCase();
        String reformatted = iban.substring(4) + iban.substring(0, 4);

        StringBuilder numericIban = new StringBuilder();
        for (char ch : reformatted.toCharArray()) {
            int value = Character.isLetter(ch) ? ch - 'A' + 10 : Character.getNumericValue(ch);
            numericIban.append(value);
        }
        try {
            BigInteger ibanNum = new BigInteger(numericIban.toString());
            return ibanNum.mod(BigInteger.valueOf(97)).intValue() == 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}