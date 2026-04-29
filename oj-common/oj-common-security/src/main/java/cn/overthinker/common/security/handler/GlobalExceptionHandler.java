package cn.overthinker.common.security.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.overthinker.common.core.domain.R;
import cn.overthinker.common.core.enums.ResultCode;
import cn.overthinker.common.security.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一捕获并处理各类异常，返回标准响应格式
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 处理请求方式不支持异常（如用 GET 访问只支持 POST 的接口）
     *
     * @param e       不支持的请求方式异常
     * @param request 当前 HTTP 请求
     * @return 统一错误响应
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<?> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不⽀持'{}'请求", requestURI, e.getMethod());
        return R.fail(ResultCode.ERROR);
    }

    /**
     * Service处理运行时异常
     *
     * @param e       Service运行时异常
     * @param request 当前 HTTP 请求
     * @return 统一错误响应
     */
    @ExceptionHandler(ServiceException.class)
    public R<?> handleServiceException(ServiceException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        ResultCode resultCode = e.getResultCode();
        log.error("请求地址'{}',发生业务异常:{}", requestURI, resultCode.getMsg(), e);
        return R.fail(resultCode);
    }


    /**
     * 处理捕获参数校验失败时的异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException e) {
        log.error(e.getMessage());
        String message = join(e.getAllErrors(),
                DefaultMessageSourceResolvable::getDefaultMessage, ", ");
        return R.fail(ResultCode.FAILED_PARAMS_VALIDATE.getCode(), message);
    }

    // 负责异常的拼装
    private <E> String join(Collection<E> collection, Function<E, String>
            function, CharSequence delimiter) {
        if (CollUtil.isEmpty(collection)) {
            return StrUtil.EMPTY;
        }
        return
                collection.stream().map(function).filter(Objects::nonNull).collect(Collectors.joining(delimiter));
    }

    /**
     * 处理运行时异常
     *
     * @param e       运行时异常
     * @param request 当前 HTTP 请求
     * @return 统一错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    public R<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发⽣运行时异常.", requestURI, e);
        return R.fail(ResultCode.ERROR);
    }

    /**
     * 处理所有未被上层捕获的系统异常（兜底处理）
     *
     * @param e       异常
     * @param request 当前 HTTP 请求
     * @return 统一错误响应
     */
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发⽣异常.", requestURI, e);
        return R.fail(ResultCode.ERROR);
    }
}