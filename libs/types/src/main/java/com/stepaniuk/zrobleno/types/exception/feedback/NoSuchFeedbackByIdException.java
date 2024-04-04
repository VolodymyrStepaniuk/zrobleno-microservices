package com.stepaniuk.zrobleno.types.exception.feedback;

import lombok.Getter;

/**
 * Exception thrown when no service with given id exists.
 *
 * @see RuntimeException
 */
@Getter
public class NoSuchFeedbackByIdException extends RuntimeException{
  private final Long id;

  public NoSuchFeedbackByIdException(Long id) {
    super("No such feedback with id: " + id);
    this.id = id;
  }
}