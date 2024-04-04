package com.stepaniuk.feedback.shared;

import com.stepaniuk.zrobleno.types.exception.feedback.NoSuchFeedbackByIdException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class FeedbackExceptionHandler {
    @ExceptionHandler(value = {NoSuchFeedbackByIdException.class})
    public ProblemDetail handleNoSuchFeedbackByIdException(NoSuchFeedbackByIdException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                "No feedback with id " + e.getId() + " found");
        problemDetail.setTitle("No such feedback");
        problemDetail.setInstance(URI.create("/feedbacks/" + e.getId()));
        return problemDetail;
    }
}
