package com.gsz.empvis.exception;

import com.gsz.empvis.common.Result;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(
            BusinessException e) {

        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * @Valid 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {

        String message = e.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        return Result.error(400, message);
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(
            ConstraintViolationException e) {

        return Result.error(400, e.getMessage());
    }

    /**
     * 权限不足
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDeniedException(
            AccessDeniedException e) {

        return Result.error(403, "没有权限访问该资源");
    }

    /**
     * 未认证
     */
    @ExceptionHandler(AuthenticationException.class)
    public Result<Void> handleAuthenticationException(
            AuthenticationException e) {

        return Result.error(401, "请先登录");
    }

    /**
     * 数据库约束异常
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result<Void> handleDataIntegrityViolationException(
            DataIntegrityViolationException e) {

        return Result.error(400, "数据操作失败，请检查相关数据");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Void> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {

        String message = "请求方式错误，当前接口仅支持 "
                + String.join(", ", e.getSupportedMethods());

        return Result.error(405, message);
    }

    /**
     * 其他异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {

        return Result.error(500, "系统异常，请稍后重试");
    }
}