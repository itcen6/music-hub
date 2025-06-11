package com.weatherdecen.musichub.common.exception;

import java.util.Map;
import java.util.function.Consumer;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    private final ErrorCode errorCode;
    private final Map<String, Object> parameters;
    private final Consumer<String> logMethod;
    private final Exception exception;

    public CustomException(ErrorCode errorCode, Map<String, Object> parameters, Consumer<String> logMethod){
        this(errorCode, parameters, logMethod, null);
    }

    public String getMessage(){
        return errorCode.getExternalErrorMessage();
    }

    public CustomException(ErrorCode errorCode, Map<String, Object> parameters, Consumer<String> logMethod, Exception exception){
        this.errorCode = errorCode;
        this.parameters = parameters;
        this.logMethod = logMethod;
        this.exception = exception;
    }
}

