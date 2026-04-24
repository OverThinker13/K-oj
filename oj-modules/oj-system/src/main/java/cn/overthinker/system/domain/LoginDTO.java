package cn.overthinker.system.domain;

import lombok.Data;

@Data
public class LoginDTO {
    private String userAccount;
    private String password;
}
