package cn.overthinker.system.controller;

import lombok.Data;

@Data
public class LoginResult {
    private int code;  //0 失败   1 成功
    private String msg;
}
