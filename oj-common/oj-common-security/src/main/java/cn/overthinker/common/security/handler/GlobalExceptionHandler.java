package cn.overthinker.common.security.handler;

import cn.overthinker.common.core.domain.R;
import cn.overthinker.common.core.enums.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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