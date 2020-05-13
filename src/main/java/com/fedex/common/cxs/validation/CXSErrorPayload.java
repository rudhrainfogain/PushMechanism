package com.fedex.common.cxs.validation;

import javax.validation.Payload;

/**
 * CXSErrorPayload provides a mechanism for the CXS error code and message to be overridden when
 * using bean validation.  To use this, implement this interface and specify the message and code that you would
 * like to return.  Then, inside the bean validation annotation, assign the <code>payload</code> to your unique
 * implementation.
 *
 * <code>
 *     public class MyCustomErrorPayload implements Payload {
 *
 *         public String getMessage() {
 *             return "My custom error message";
 *         }
 *
 *         public String getCode() {
 *             return "MY.CUSTOM.CODE";
 *         }
 *     }
 *
 *     public class MyRequestDto {
 *         {@literal @}NotEmpty(payload = {MyCustomErrorPayload.class})
 *         private String id;
 *     }
 * </code>
 */
public interface CXSErrorPayload extends Payload {

    /**
     * The exact content to place into a CXS Error Message (note that these are controlled and need
     * to be approved by marketing).
     */
    String getMessage();

    /**
     * The exact content to place into a CXS Error Code (note that these are controlled and need to
     * be approved by marketing).
     */
    String getCode();
}
