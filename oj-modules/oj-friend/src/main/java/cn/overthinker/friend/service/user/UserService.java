package cn.overthinker.friend.service.user;

import cn.overthinker.common.core.domain.R;
import cn.overthinker.common.core.domain.vo.LoginUserVO;
import cn.overthinker.friend.domain.user.dto.UserDTO;

public interface UserService {
    boolean sendCode(UserDTO userDTO);

    String codeLogin(String phone, String code);

    boolean logout(String token);

    R<LoginUserVO> info(String token);
}
