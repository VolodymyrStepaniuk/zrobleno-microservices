package com.stepaniuk.zrobleno.types.exception.order;

import lombok.Getter;

/**
 * Exception thrown when no order with given id exists.
 *
 * @see RuntimeException
 */
@Getter
public class NoSuchOrderByIdException extends RuntimeException {

  private final Long id;

  public NoSuchOrderByIdException(Long id) {
    super("No such order with id: " + id);
    this.id = id;
  }

}
