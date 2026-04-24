package cn.overthinker.common.core.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseEntity {
    private Long createBy;
    private Long updateBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
