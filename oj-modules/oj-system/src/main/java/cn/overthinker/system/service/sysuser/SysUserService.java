package cn.overthinker.system.service.sysuser;

import cn.overthinker.common.core.domain.R;
import cn.overthinker.common.core.domain.vo.LoginUserVO;
import cn.overthinker.system.domain.sysuser.dto.SysUserSaveDTO;

public interface SysUserService {
    R<String> login(String userAccount, String password);

    boolean logout(String token);

    R<LoginUserVO> info(String token);

    int add(SysUserSaveDTO sysUserSaveDTO);


}
