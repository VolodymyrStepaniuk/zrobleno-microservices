package com.stepaniuk.zrobleno.types.exception.service;

import lombok.Getter;

/**
 * Exception thrown when no service with given id exists.
 *
 * @see RuntimeException
 */
@Getter
public class NoSuchServiceByIdException extends RuntimeException{
  private final Long id;

  public NoSuchServiceByIdException(Long id) {
    super("No such service with id: " + id);
    this.id = id;
  }
}
