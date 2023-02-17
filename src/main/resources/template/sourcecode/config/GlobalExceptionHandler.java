package ${package}.config;

import ${package}.dto.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.ServletException;
import javax.validation.ConstraintViolationException;
import java.util.Objects;

import static ${package}.constant.ApiStatus.*;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
@SuppressWarnings("rawtypes")
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResult handleConstraintViolationException(ConstraintViolationException e) {
        log.error("参数校验失败，" + e.getMessage(), e);
        return ApiResult.failure(PARAM_ERROR.getStatus(), "参数校验失败，" + e.getMessage());
    }

    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public ApiResult handleBindException(Exception e) {
        BindingResult bindingResult;
        if (e instanceof BindException) {
            bindingResult = ((BindException) e).getBindingResult();
        } else {
            bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
        }

        log.error(String.format("参数校验失败，被校验的目标对象：%s", bindingResult.getTarget()), e);

        FieldError fieldError = bindingResult.getFieldError();
        String message = "参数校验失败，";
        if (Objects.nonNull(fieldError)) {
            message += fieldError.getDefaultMessage();
        }
        return ApiResult.failure(PARAM_ERROR.getStatus(), message);
    }

    @ExceptionHandler(Throwable.class)
    public ApiResult handleThrowable(Throwable e) {
        if (e instanceof IllegalArgumentException) {
            log.error("", e);
            return ApiResult.failure(PARAM_ERROR.getStatus(), e.getMessage());
        } else if (e instanceof ServletException) {
            log.error("", e);
            return ApiResult.failure(BAD_REQUEST.getStatus(), e.getMessage());
        } else {
            log.error("", e);
            return ApiResult.failure(SERVER_ERROR.getStatus(), SERVER_ERROR.getMessage());
        }
    }

}
