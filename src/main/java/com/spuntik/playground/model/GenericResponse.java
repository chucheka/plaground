package com.spuntik.playground.model;

import lombok.Data;

@Data
public class GenericResponse<T> {

    private String status;

    private String message;

    private T data;


}
