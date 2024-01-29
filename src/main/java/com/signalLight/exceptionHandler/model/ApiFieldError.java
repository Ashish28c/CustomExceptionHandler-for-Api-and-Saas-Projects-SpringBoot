package com.signalLight.exceptionHandler.model;

import java.io.Serializable;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiFieldError implements Serializable {

  private static final long serialVersionUID = 1L;
private String object;
  private String field;
  private String message;
}
