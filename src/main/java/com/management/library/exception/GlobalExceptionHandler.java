package com.management.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DuplicateException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponseDto handleDuplicateException(DuplicateException e){
    return ErrorResponseDto.builder()
        .errorCode(e.getErrorCode())
        .errorMessage(e.getDescription())
        .build();
  }

  @ExceptionHandler(InvalidAccessException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponseDto handleInvalidAccessException(InvalidAccessException e){
    return ErrorResponseDto.builder()
        .errorCode(e.getErrorCode())
        .errorMessage(e.getDescription())
        .build();
  }

  @ExceptionHandler(InvalidArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponseDto handleInvalidArgumentException(InvalidArgumentException e){
    return ErrorResponseDto.builder()
        .errorCode(e.getErrorCode())
        .errorMessage(e.getDescription())
        .build();
  }

  @ExceptionHandler(NoSuchElementExistsException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponseDto handleNoSuchElementException(NoSuchElementExistsException e){
    return ErrorResponseDto.builder()
        .errorCode(e.getErrorCode())
        .errorMessage(e.getDescription())
        .build();
  }

  @ExceptionHandler(RedisLockException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponseDto handleRedisLockException(RedisLockException e){
    return ErrorResponseDto.builder()
        .errorCode(e.getErrorCode())
        .errorMessage(e.getDescription())
        .build();
  }

  @ExceptionHandler(RentalException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponseDto handleRentalException(RentalException e){
    return ErrorResponseDto.builder()
        .errorCode(e.getErrorCode())
        .errorMessage(e.getDescription())
        .build();
  }

  @ExceptionHandler(RequestLimitExceededException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponseDto handleRequestLimitExceedException(RequestLimitExceededException e){
    return ErrorResponseDto.builder()
        .errorCode(e.getErrorCode())
        .errorMessage(e.getDescription())
        .build();
  }

  @ExceptionHandler(LoginFailedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponseDto loginFailedExceptionHandler(LoginFailedException e){
    return ErrorResponseDto.builder()
        .errorCode(e.getErrorCode())
        .errorMessage(e.getDescription())
        .build();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponseDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    BindingResult bindingResult = e.getBindingResult();

    StringBuilder sb = new StringBuilder();

    for (var error: bindingResult.getFieldErrors()){
      sb.append(error.getField()).append(": ");
      sb.append(error.getDefaultMessage()).append(", ");
    }

    return ErrorResponseDto.builder()
        .errorCode(ErrorCode.VALIDATION_FAILED)
        .errorMessage(sb.toString())
        .build();
  }

}
