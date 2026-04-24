package cn.overthinker.system.domain;

import cn.overthinker.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("tb_sys_user")
@Data
public class SysUser extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)  // 通过雪花算法来生成主键id
    private Long userId;  // 主键 不再使用auto——increment
    private String userAccount;
    private String password;
    private Long createBy;
    private Long updateBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
