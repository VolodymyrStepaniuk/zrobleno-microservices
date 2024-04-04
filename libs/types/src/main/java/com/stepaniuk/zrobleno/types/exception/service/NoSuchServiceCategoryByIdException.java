package com.stepaniuk.zrobleno.types.exception.service;

import lombok.Getter;

/**
 * Exception thrown when no service category with given id exists.
 *
 * @see RuntimeException
 */
@Getter
public class NoSuchServiceCategoryByIdException extends RuntimeException {

  private final Long id;

  public NoSuchServiceCategoryByIdException(Long id) {
    super("No such service category with id: " + id);
    this.id = id;
  }
}