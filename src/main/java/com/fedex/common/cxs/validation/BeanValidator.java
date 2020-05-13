package com.fedex.common.cxs.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Helper class to aid when working with bean validation
 */
@Component
public class BeanValidator {

    private final Validator validator;

    @Autowired
    public BeanValidator(final Validator validator) {
        this.validator = validator;
    }

    /**
     * Convenience method to execute the validator and throw an exception if constraints are violated
     * @param dto The object to be validated
     * @throws ConstraintViolationException if any constraints are broken
     */
    public <T> void assertValid(final T dto) {
        final Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

}
