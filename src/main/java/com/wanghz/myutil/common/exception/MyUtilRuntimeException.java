package com.wanghz.myutil.common.exception;

/**
 * MyUtil自定义运行时错误
 * @author wanghz
 * @since 2020年10月9日
 */
public class MyUtilRuntimeException extends RuntimeException {

    public MyUtilRuntimeException(String message) {
        super(message);
    }

    public MyUtilRuntimeException(Throwable cause) {
        super(cause);
    }

    public MyUtilRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
