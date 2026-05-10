package cn.overthinker.system.domain.user.dto;

import cn.overthinker.common.core.domain.dto.PageQueryDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserQueryDTO extends PageQueryDTO {

    private Long userId;

    private String nickName;
}
