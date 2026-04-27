package cn.overthinker.system.service;

import cn.overthinker.common.core.domain.R;
import cn.overthinker.system.domain.SysUserSaveDTO;

public interface SysUserService {
    R<String> login(String userAccount, String password);

    int add(SysUserSaveDTO sysUserSaveDTO);
}
