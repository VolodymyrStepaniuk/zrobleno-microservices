package com.stepaniuk.order.shared;

import com.stepaniuk.zrobleno.types.exception.order.IllegalOrderStatusException;
import com.stepaniuk.zrobleno.types.exception.order.NoSuchOrderByIdException;
import com.stepaniuk.zrobleno.types.exception.order.NoSuchOrderStatusByNameException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class OrderExceptionHandler {
    @ExceptionHandler(value = {IllegalOrderStatusException.class})
    public ProblemDetail handleIllegalOrderStatusException(IllegalOrderStatusException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Illegal order status! From " + e.getPreviousStatus() + " to " + e.getStatus() + " in order with id: " + e.getId());
        problemDetail.setTitle("Illegal order status");
        problemDetail.setInstance(URI.create("/orders/" + e.getId()));
        return problemDetail;
    }

    @ExceptionHandler(value = {NoSuchOrderByIdException.class})
    public ProblemDetail handleNoSuchOrderByIdException(NoSuchOrderByIdException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                "No order with id " + e.getId() + " found");
        problemDetail.setTitle("No such order");
        problemDetail.setInstance(URI.create("/orders/" + e.getId()));
        return problemDetail;
    }

    @ExceptionHandler(value = {NoSuchOrderStatusByNameException.class})
    public ProblemDetail handleNoSuchOrderStatusByNameException(NoSuchOrderStatusByNameException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                "No order status with name " + e.getName() + " found");
        problemDetail.setTitle("No such order status");
        problemDetail.setInstance(URI.create("/orders/status/" + e.getName()));
        return problemDetail;
    }
}
