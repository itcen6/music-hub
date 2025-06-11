package com.weatherdecen.musichub.common.exception;

import com.google.common.base.Joiner;
import com.weatherdecen.musichub.common.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @Order(1)
    @ExceptionHandler(value={CustomException.class})
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException e){
        Map<String, Object> parameters = e.getParameters();

        String join = Joiner.on(",").withKeyValueSeparator("=").join(parameters);
        log.error(join);
        log.error("CustomException occured : " + e.getErrorCode().getInternalErrorMessage());

        return ResponseEntity
            .status(e.getErrorCode().getHttpStatus())
            .body(ApiResponse.createFail(e));
    }
    @Order(2)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolationException(ConstraintViolationException e) {

        String errorMessage = ExceptionUtils.getStackTrace(e);
        CustomException customException = new CustomException(ErrorCode.VALIDATION_ERROR, Map.of("exception", errorMessage), log::info);

        log.error("ConstraintViolationException occured : " + customException.getErrorCode().getInternalErrorMessage());

        return new ResponseEntity<>(ApiResponse.createFailWithErrorMessage(customException.getErrorCode().getExternalErrorMessage()), customException.getErrorCode()
            .getHttpStatus());
    }

    @Order(3)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {

        String errorMessage = ExceptionUtils.getStackTrace(e);
        CustomException customException = new CustomException(ErrorCode.MISSING_PARAMETER, Map.of("exception", errorMessage), log::info);

        log.error("MissingServletRequestParameterException occured : " + customException.getErrorCode().getInternalErrorMessage());

        return new ResponseEntity<>(ApiResponse.createFailWithErrorMessage(customException.getErrorCode().getExternalErrorMessage()), customException.getErrorCode()
            .getHttpStatus());
    }

    @Order(99)
    @ExceptionHandler(value={Exception.class})
    public ResponseEntity<ApiResponse<?>> handleException(Exception e){

        String errorMessage = ExceptionUtils.getStackTrace(e);
        CustomException customException = new CustomException(ErrorCode.INTERNAL_ERROR, Map.of("exception", errorMessage), log::info);

        log.error("Exception occured : " + customException.getErrorCode().getInternalErrorMessage());
        log.error("Exception parameter : " + customException.getParameters());
        log.error("Exception logInfo : " + errorMessage);
        return new ResponseEntity<>(ApiResponse.createFailWithErrorMessage(e.getMessage()), customException.getErrorCode()
            .getHttpStatus());
    }
}


