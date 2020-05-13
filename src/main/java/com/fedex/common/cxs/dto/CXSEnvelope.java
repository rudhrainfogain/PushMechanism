package com.fedex.common.cxs.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * This wrapper follows the FedEx CXS Envelope 3.5 format.  It also serves as a translation layer between
 * Jackson and the objects that the application uses internally, namely outputs.
 *
 * <code>
 * {
 *   "transactionId": "string",
 *   "output": {
 *     "alerts": [
 *       {
 *         "code": "string",
 *         "message": "string",
 *         "alertType": "WARNING"
 *       }
 *     ],
 *     "yourCustomResponseContent": {
 *       // YOUR CUSTOM CONTENT GOES HERE!
 *     }
 *   },
 *   "errors": [
 *     {
 *       "code": "string",
 *       "message": "string"
 *     }
 *   ]
 * }
 * </code>
 *
 * @author Ben Bays <ben.bays@projekt202.com>
 * @version 3.5
 * @see CXSOutput
 * @see CXSError
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class CXSEnvelope <T extends CXSOutput> {

    private String transactionId;

    private List<CXSError> errors = new ArrayList<>();

    private T output;


    /**
     * Default constructor used for jackson (de)serialization and when no properties exist (they're all optional)
     */
    private CXSEnvelope() {
    }

    /**
     * Default constructor for a successful envelope, but no relevant output
     */
    private CXSEnvelope(final String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Create a CXSEnvelope that contains output and a transactionId
     */
    private CXSEnvelope(final String transactionId, final T output) {
        this.transactionId = transactionId;
        this.output = output;
    }

    /**
     * Create a CXSEnvelope that does not contain a transactionId
     */
    private CXSEnvelope(final T output) {
        this(null, output);
    }

    /**
     * Create a CXSEnvelope that contains errors
     */
    private CXSEnvelope(final String transactionId, final List<CXSError> errors) {
        this.transactionId = transactionId;
        this.errors = errors;
    }

    /**
     * Create a CXSEnvelope that contains errors
     */
    private CXSEnvelope(final String transactionId, final CXSError ... errors) {
        this(transactionId, Arrays.asList(errors));
    }

    /**
     * Create a CXS Envelope that contains a single error
     */
    private CXSEnvelope(final String transactionId, final String errorCode, final String errorMessage) {
        this(transactionId, new CXSError(errorCode, errorMessage));
    }

    /**
     * Create a CXS Envelope that has no transaction id or outputs
     */
    public static CXSEnvelope success() {
        return new CXSEnvelope();
    }

    /**
     * Create a CXS Envelope that is successful, but has no relevant outputs.
     */
    public static CXSEnvelope success(final String transactionId) {
        return new CXSEnvelope(transactionId);
    }

    /**
     * Create a CXSEnvelope that contains output and a transactionId
     */
    public static <R extends CXSOutput> CXSEnvelope<R> success(final String transactionId, final R output) {
        return new CXSEnvelope<>(output);
    }

    /**
     * Create a CXSEnvelope that does not contain a transactionId
     */
    public static <R extends CXSOutput> CXSEnvelope<R> success(final R output) {
        return new CXSEnvelope<>(output);
    }

    /**
     * Build a CXSEnvelope that contains errors
     */
    public static CXSEnvelope error(
            final String transactionId,
            final String errorCode,
            final String errorMessage) {
        return new CXSEnvelope<>(transactionId, errorCode, errorMessage);
    }

    /**
     * Build a CXSEnvelope that contains errors
     */
    public static CXSEnvelope error(final String errorCode, final String errorMessage) {
        return new CXSEnvelope<>(null, errorCode, errorMessage);
    }

    /**
     * Build a CXSEnvelope that contains errors
     */
    public static CXSEnvelope error(final String transactionId, final List<CXSError> errors) {
        return new CXSEnvelope<>(transactionId, errors);
    }

    /**
     * Build a CXSEnvelope that contains errors
     */
    public static CXSEnvelope error(final List<CXSError> errors) {
        return new CXSEnvelope<>(null, errors);
    }

    /**
     * Build a CXSEnvelope that contains errors
     */
    public static CXSEnvelope error(
            final String transactionId,
            final CXSError ... errors) {
        return new CXSEnvelope<>(transactionId, errors);
    }

    /**
     * Build a CXSEnvelope that contains errors
     */
    public static CXSEnvelope error(final CXSError ... errors) {
        return new CXSEnvelope<>(null, errors);
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public List<CXSError> getErrors() {
        return errors;
    }

    public T getOutput() {
        return output;
    }

    @Override
    public String toString() {
        return "CXSEnvelope{" + "transactionId='" + transactionId + '\'' + ", errors=" + errors + ", output=" + output
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CXSEnvelope)) {
            return false;
        }
        CXSEnvelope<?> that = (CXSEnvelope<?>) o;
        return Objects.equals(transactionId, that.transactionId) && Objects.equals(errors, that.errors)
                && Objects.equals(output, that.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, errors, output);
    }
}
