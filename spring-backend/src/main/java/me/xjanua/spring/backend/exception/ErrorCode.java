package me.xjanua.spring.backend.exception;

public final class ErrorCode {

    public static final String BAD_REQUEST = "BAD_REQUEST";
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String INVALID_REQUEST_BODY = "INVALID_REQUEST_BODY";
    public static final String INVALID_ARGUMENT = "INVALID_ARGUMENT";
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    public static final String EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS";
    public static final String PASSWORD_MISMATCH = "PASSWORD_MISMATCH";
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String PERMISSION_NOT_FOUND = "PERMISSION_NOT_FOUND";
    public static final String ROLE_NOT_FOUND = "ROLE_NOT_FOUND";
    public static final String ROLE_PERMISSION_NOT_FOUND = "ROLE_PERMISSION_NOT_FOUND";
    public static final String ROLE_PERMISSION_ALREADY_EXISTS = "ROLE_PERMISSION_ALREADY_EXISTS";
    public static final String SHORT_LINK_NOT_FOUND = "SHORT_LINK_NOT_FOUND";
    public static final String SHORT_CODE_ALREADY_EXISTS = "SHORT_CODE_ALREADY_EXISTS";
    public static final String SHORT_LINK_DELETED = "SHORT_LINK_DELETED";
    public static final String SHORT_LINK_GONE = "SHORT_LINK_GONE";
    public static final String SHORT_LINK_PASSWORD_REQUIRED = "SHORT_LINK_PASSWORD_REQUIRED";
    public static final String INVALID_SHORT_LINK_PASSWORD = "INVALID_SHORT_LINK_PASSWORD";
    public static final String CLICK_DATE_RANGE_REQUIRED = "CLICK_DATE_RANGE_REQUIRED";
    public static final String INVALID_CLICK_DATE_RANGE = "INVALID_CLICK_DATE_RANGE";

    private ErrorCode() {
    }
}
