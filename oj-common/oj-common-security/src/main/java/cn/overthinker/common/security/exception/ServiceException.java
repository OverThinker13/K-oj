package cn.overthinker.common.security.exception;

import cn.overthinker.common.core.enums.ResultCode;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private ResultCode resultCode;

    public ServiceException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }
}
