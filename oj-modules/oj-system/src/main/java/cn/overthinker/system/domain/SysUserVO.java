package cn.overthinker.system.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysUserVO {
    @Schema(description = "用户账户")
    private String userAccount;
    @Schema(description = "用户昵称")
    private String nickName;
}
