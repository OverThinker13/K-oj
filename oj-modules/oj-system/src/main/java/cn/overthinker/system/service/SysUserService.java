package cn.overthinker.system.service;

import cn.overthinker.common.core.domain.R;
import cn.overthinker.common.core.domain.vo.LoginUserVO;
import cn.overthinker.system.domain.dto.SysUserSaveDTO;

public interface SysUserService {
    R<String> login(String userAccount, String password);

    int add(SysUserSaveDTO sysUserSaveDTO);

    R<LoginUserVO> info(String token);
}
