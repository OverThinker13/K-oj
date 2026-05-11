package cn.overthinker.friend.service.user;

import cn.overthinker.friend.domain.user.dto.UserDTO;

public interface UserService {
    boolean sendCode(UserDTO userDTO);

    String codeLogin(String phone, String code);
}
