package com.seafile.seadroid2;

import com.google.common.base.Objects;

/**
 * The type Seaf exception.
 */
public class SeafException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * The constant OTHER_EXCEPTION.
     */
    public static final int OTHER_EXCEPTION = 599;

    private int code;

    /**
     * The constant unknownException.
     */
    public static final SeafException unknownException = new SeafException(1, "Unknown Error");
    /**
     * The constant networkException.
     */
    public static final SeafException networkException = new SeafException(2, "Network Error");
    /**
     * The constant encodingException.
     */
    public static final SeafException encodingException = new SeafException(3, "Encoding Error");
    /**
     * The constant illFormatException.
     */
    public static final SeafException illFormatException = new SeafException(4, "Ill-formatted Response");
    /**
     * The constant sslException.
     */
    public static final SeafException sslException = new SeafException(5, "not trusted SSL server");
    /**
     * The constant userCancelledException.
     */
    public static final SeafException userCancelledException = new SeafException(6, "operation cancelled by user");
    /**
     * The constant invalidPassword.
     */
    public static final SeafException invalidPassword = new SeafException(7, "wrong password");
    /**
     * The constant unsupportedEncVersion.
     */
    public static final SeafException unsupportedEncVersion = new SeafException(8, "unsupported encryption version");
    /**
     * The constant blockListNullPointerException.
     */
    public static final SeafException blockListNullPointerException = new SeafException(9, "block list is null");
    /**
     * The constant encryptException.
     */
    public static final SeafException encryptException = new SeafException(10, "encryption key or iv is null");
    /**
     * The constant decryptException.
     */
    public static final SeafException decryptException = new SeafException(11, "decryption key or iv is null");
    /**
     * The constant remoteWipedException.
     */
    public static final SeafException remoteWipedException = new SeafException(12, "Remote Wiped Error");
    /**
     * The constant twoFactorAuthTokenMissing.
     */
    public static final SeafException twoFactorAuthTokenMissing = new SeafException(13, "Two factor auth token is missing");
    /**
     * The constant twoFactorAuthTokenInvalid.
     */
    public static final SeafException twoFactorAuthTokenInvalid = new SeafException(14, "Two factor auth token is invalid");

    /**
     * Instantiates a new Seaf exception.
     *
     * @param code the code
     * @param msg  the msg
     */
    public SeafException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    public int getCode() {
        return code;
    }

    public String toString() {
        return Objects.toStringHelper(this)
            .add("code", code)
            .add("msg", getMessage())
            .toString();
    }
}
