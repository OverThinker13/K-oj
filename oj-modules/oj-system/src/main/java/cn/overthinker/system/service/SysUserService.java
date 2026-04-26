package cn.overthinker.system.service;

import cn.overthinker.common.core.domain.R;

public interface SysUserService {
    R<String> login(String userAccount, String password);
}
