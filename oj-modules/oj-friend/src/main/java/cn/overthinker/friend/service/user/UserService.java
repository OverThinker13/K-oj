package cn.overthinker.friend.service.user;

import cn.overthinker.friend.domain.user.dto.UserDTO;

public interface UserService {
    void sendCode(UserDTO userDTO);
}
