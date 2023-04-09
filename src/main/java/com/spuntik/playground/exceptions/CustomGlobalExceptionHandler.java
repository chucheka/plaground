package com.spuntik.playground.exceptions;

import com.spuntik.playground.model.GenericResponse;
import io.netty.handler.timeout.ReadTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({AppException.class, ConnectException.class, SocketTimeoutException.class, ReadTimeoutException.class, TimeoutException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<GenericResponse> handleAppException(Exception exc){
        GenericResponse response = new GenericResponse();

        response.setData(exc.getLocalizedMessage());
        response.setStatus("FAILURE");
        response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<GenericResponse> handleBadRequestException(BadRequestException exc){
        GenericResponse response = new GenericResponse();

        response.setData(exc.getLocalizedMessage());
        response.setStatus("FAILURE");
        response.setMessage(HttpStatus.BAD_REQUEST.name());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(WebClientResponseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<GenericResponse> handleWebClientResponseException(WebClientResponseException ex) {


        GenericResponse response = new GenericResponse();

        response.setData(ex.getResponseBodyAsString());
        response.setStatus("FAILURE");
        response.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());

        return ResponseEntity.status(ex.getRawStatusCode()).body(response);
    }
}