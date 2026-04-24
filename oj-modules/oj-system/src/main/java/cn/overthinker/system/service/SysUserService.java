package cn.overthinker.system.service;

import cn.overthinker.common.core.domain.R;

public interface SysUserService {
    R<Void> login(String userAccount, String password);
}
