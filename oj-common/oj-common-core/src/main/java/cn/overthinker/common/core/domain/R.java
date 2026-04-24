package cn.overthinker.common.core.domain;


import lombok.Data;

@Data
public class R<T> {
    private int code;
    private String msg;
    private T data;
}

