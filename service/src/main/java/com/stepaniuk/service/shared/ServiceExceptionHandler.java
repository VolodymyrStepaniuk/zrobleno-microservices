package com.stepaniuk.service.shared;

import com.stepaniuk.zrobleno.types.exception.service.NoSuchServiceByIdException;
import com.stepaniuk.zrobleno.types.exception.service.NoSuchServiceCategoryByIdException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class ServiceExceptionHandler {
    @ExceptionHandler(value = {NoSuchServiceByIdException.class})
    public ProblemDetail handleNoSuchServiceByIdException(NoSuchServiceByIdException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                "No service with id " + e.getId() + " found");
        problemDetail.setTitle("No such service");
        problemDetail.setInstance(URI.create("/services/" + e.getId()));
        return problemDetail;
    }

    @ExceptionHandler(value = {NoSuchServiceCategoryByIdException.class})
    public ProblemDetail handleNoSuchServiceCategoryByIdException(NoSuchServiceCategoryByIdException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                "No service category with id " + e.getId() + " found");
        problemDetail.setTitle("No such service category");
        problemDetail.setInstance(URI.create("/services/categories/" + e.getId()));
        return problemDetail;
    }
}
