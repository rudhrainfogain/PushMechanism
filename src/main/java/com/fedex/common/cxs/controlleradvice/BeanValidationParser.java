package com.fedex.common.cxs.controlleradvice;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

/**
 * This component serves to parse a {@link ConstraintViolation} into the pieces we need to build
 * an error response.  This typically includes the path to the property in violation (section name
 * and field name), as well as the type of violation.
 *
 * @author Ben Bays <ben.bays@projekt202.com>
 * @since 2018-10-22
 */
@SuppressWarnings("WeakerAccess")
@Component
public class BeanValidationParser {

    /**
     * List of javax.validation annotations that imply a required property is absent
     */
    private final static List<Class> MISSING_PROPERTY_ANNOTATIONS = Arrays.asList(
            NotBlank.class,
            NotEmpty.class,
            NotNull.class
    );

    /**
     * List of javax.validation annotations that imply a string/collection/array is of improper length
     */
    private final static List<Class> INVALID_LENGTH_ANNOTATIONS = Collections.singletonList(Size.class);

    /**
     * List of javax.validation annotations that imply a value is invalid
     */
    private final static List<Class> INVALID_VALUE_ANNOTATIONS = Arrays.asList(
            AssertFalse.class,      // value must be false
            AssertTrue.class,       // value must be true
            DecimalMax.class,       // upper bound on numerical value (could be represented as String)
            DecimalMin.class,       // lower bound on numerical value (could be represented as String)
            Digits.class,           // upper & lower bound on numerical value
            Email.class,            // value must follow format of e-mail address
            Future.class,           // date must be in the future
            FutureOrPresent.class,  // date must be now() or in the future
            Max.class,              // upper bound on numerical value
            Min.class,              // lower bound on numerical value
            Negative.class,         // numerical value must be negative
            NegativeOrZero.class,   // numerical value must be negative or zero
            Null.class,             // value must be null
            Past.class,             // date must be in the past
            PastOrPresent.class,    // date must be now() or in the past
            Pattern.class,          // value must adhere to a regex
            Positive.class,         // value must be positive
            PositiveOrZero.class    // value must be positive or zero
    );

    private final ObjectMapper objectMapper;

    @Autowired
    public BeanValidationParser(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Determine what type of violation has occurred based on the type of javax.validation annotation
     * triggered the violation error
     * @param violation The violation contained within an ConstraintViolationException
     * @return {@link ValidationErrorType} indicating which type of error has occurred
     */
    public ValidationErrorType getErrorType(final ConstraintViolation violation) {
        // get the class name of the annotation that caused failure
        final Class violationClazz = getViolationClass(violation);


        if (null == violationClazz) {
            throw new IllegalArgumentException("Unable to determine validation annotation class");
        } else if (MISSING_PROPERTY_ANNOTATIONS.contains(violationClazz)) {
            return ValidationErrorType.MISSING_PROPERTY;
        } else if (INVALID_LENGTH_ANNOTATIONS.contains(violationClazz)) {
            return ValidationErrorType.INVALID_LENGTH;
        } else if (INVALID_VALUE_ANNOTATIONS.contains(violationClazz)) {
            return ValidationErrorType.INVALID_VALUE;
        } else {
            return ValidationErrorType.UNKNOWN;
        }
    }

    /**
     * Determine the field and section names (if one exists), indicating which property is in violation
     * @param violation The  {@link ConstraintViolation} contained in the violation exception
     * @return {@link PropertyPathNames} indicating both field and section names, <code>null</code> if no path exists
     * @throws NoSuchFieldException Unable to find field by name
     */
    public PropertyPathNames getPropertyPathNames(final ConstraintViolation violation) throws NoSuchFieldException {
        final Path propertyPath = violation.getPropertyPath();

        if (null == propertyPath || propertyPath.toString().isEmpty()) {
            return null;
        }

        final String[] pathElements = propertyPath.toString().split("\\.");

        // The bean that has invalid data (i.e. the DTO object that had a java validation annotation on it)
        Class beanClazz = violation.getRootBeanClass();

        // in the event that this is a heavily nested request document, we need to walk the path until
        // we get to the last two elements (last element = field name, optional second to last element = section name)
        String sectionName = null;
        String fieldName = null;

        for (int i = 0; i < pathElements.length; i++) {

            final String pathElement = pathElements[i];

            if (pathElements.length - i > 2) {
                // if the object being validated is highly nested (contains more than just the field name
                // and a single section, then skip the ObjectMapper business and reassign the beanClazz
                // so that we can walk the path without the overhead of asking jackson to introspec properties
                beanClazz = beanClazz.getDeclaredField(pathElement).getType();
            } else {

                // it is possible that jackson annotations have been applied (i.e. JsonProperty(...)) that
                // rename a property on an API so that it no longer matches the bean property name.  For that
                // reason, we ask Jackson what a field is named from its perspective.  The reason we do this
                // rather than checking for a JsonProperty annotation is that there are multiple ways that
                // a property could be renamed, so it's easier to just ask jackson what its final answer is
                final JavaType javaType = objectMapper.getTypeFactory().constructType(beanClazz);
                final BeanDescription beanDescription = objectMapper.getSerializationConfig().introspect(javaType);

                // there is no api to find a property by name, instead we have to grab em' all and search.
                final List<BeanPropertyDefinition> beanProperties = beanDescription.findProperties();
                final Optional<BeanPropertyDefinition> beanPropertyDef = beanProperties.stream()
                        .filter(p -> p.getInternalName().equals(pathElement))
                        .findFirst();

                if (!beanPropertyDef.isPresent()) {
                    // this shouldn't happen since we're starting with a path to walk (we're not free forming
                    // walking this path).  Although it shouldn't happen, better safe than sorry
                    throw new NoSuchFieldException("Field " + pathElement + " does not exist");
                }

                // here's the whole reason we did this:  dear miss jackson, what do you think this property is named?
                final String apiPropertyName = beanPropertyDef.get().getName();

                if (i == pathElements.length - 2) {
                    // currently looking at section name
                    sectionName = apiPropertyName;
                } else if (i == pathElements.length - 1) {
                    // currently looking at field name
                    fieldName = apiPropertyName;
                }

                // continue walking the property path until we reach the end
                beanClazz = beanPropertyDef.get().getRawPrimaryType();
            }
        }


        return (sectionName == null) ? new PropertyPathNames(fieldName) : new PropertyPathNames(sectionName, fieldName);
    }

    /**
     * When dealing with a {@link javax.validation.ConstraintViolationException}, this method takes the
     * contained {@link ConstraintViolation} and determines which javax.validation (or custom) annotation was behind
     * the violation being thrown.
     * @param violation {@link ConstraintViolation} contained within a violation exception
     * @return {@link Class} of the validation annotation behind throwing the validation exception
     */
    private Class getViolationClass(final ConstraintViolation violation) {
        final ConstraintDescriptor descriptor = violation.getConstraintDescriptor();
        Class violationClazz = null;
        if (descriptor instanceof ConstraintDescriptorImpl) {
            // only known implementation of ConstraintDescription (behind runtime proxy)
            violationClazz = ((ConstraintDescriptorImpl) descriptor).getAnnotationType();
        } else if (AopUtils.isJdkDynamicProxy(descriptor)) {
            // Plan B: Try and dereference the Proxy since we only want the class type
            violationClazz = AopUtils.getTargetClass(descriptor);
        }

        return violationClazz;
    }

}