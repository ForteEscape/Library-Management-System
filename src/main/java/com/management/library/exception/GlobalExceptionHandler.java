package com.management.library.exception;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DuplicateException.class)
  public ErrorResponseDto handleDuplicateException(DuplicateException e){
    return ErrorResponseDto.builder()
        .errorCode(e.getErrorCode())
        .errorMessage(e.getMessage())
        .build();
  }

  @ExceptionHandler(InvalidAccessException.class)
  public ErrorResponseDto handleInvalidAccessException(InvalidAccessException e){
    return ErrorResponseDto.builder()
        .errorCode(e.getErrorCode())
        .errorMessage(e.getMessage())
        .build();
  }

  @ExceptionHandler(InvalidArgumentException.class)
  public ErrorResponseDto handleInvalidArgumentException(InvalidArgumentException e){
    return ErrorResponseDto.builder()
        .errorCode(e.getErrorCode())
        .errorMessage(e.getMessage())
        .build();
  }

  @ExceptionHandler(NoSuchElementExistsException.class)
  public ErrorResponseDto handleNoSuchElementException(NoSuchElementExistsException e){
    return ErrorResponseDto.builder()
        .errorCode(e.getErrorCode())
        .errorMessage(e.getMessage())
        .build();
  }

  @ExceptionHandler(RedisLockException.class)
  public ErrorResponseDto handleRedisLockException(RedisLockException e){
    return ErrorResponseDto.builder()
        .errorCode(e.getErrorCode())
        .errorMessage(e.getMessage())
        .build();
  }

  @ExceptionHandler(RentalException.class)
  public ErrorResponseDto handleRentalException(RentalException e){
    return ErrorResponseDto.builder()
        .errorCode(e.getErrorCode())
        .errorMessage(e.getMessage())
        .build();
  }

  @ExceptionHandler(RequestLimitExceededException.class)
  public ErrorResponseDto handleRequestLimitExceedException(RequestLimitExceededException e){
    return ErrorResponseDto.builder()
        .errorCode(e.getErrorCode())
        .errorMessage(e.getMessage())
        .build();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
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
