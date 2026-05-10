package cn.overthinker.system.service.user;

import cn.overthinker.system.domain.user.dto.UserDTO;
import cn.overthinker.system.domain.user.dto.UserQueryDTO;
import cn.overthinker.system.domain.user.vo.UserVO;

import java.util.List;

public interface UserService {
    List<UserVO> list(UserQueryDTO userQueryDTO);

    int updateStatus(UserDTO userDTO);
}
