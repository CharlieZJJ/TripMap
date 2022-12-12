package com.ecnu.tripmap.Aspect;

import com.ecnu.tripmap.result.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Response exceptionHandler(Exception exception) {
        log.error("[{}]: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return Response.exception(exception);
    }

}
