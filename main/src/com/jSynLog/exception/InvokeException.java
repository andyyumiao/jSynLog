package com.jSynLog.exception;

/**
 * Package: com.jSynLog.exception
 * Date: 15-3-16
 * Time: 下午3:42
 * Description:
 */
public class InvokeException extends Exception {
    private Integer code;

    private String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public InvokeException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public InvokeException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public InvokeException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}
