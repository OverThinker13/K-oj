package cn.overthinker.system.domain.sysuser.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

// 参数合法性
@Data
public class SysUserSaveDTO {
    @Schema(description = "用户账户")
    private String userAccount;
    @Schema(description = "用户密码")
    private String password;
}
