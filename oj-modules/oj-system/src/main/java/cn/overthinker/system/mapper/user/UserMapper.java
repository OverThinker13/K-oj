package cn.overthinker.system.mapper.user;

import cn.overthinker.system.domain.user.User;
import cn.overthinker.system.domain.user.dto.UserQueryDTO;
import cn.overthinker.system.domain.user.vo.UserVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    List<UserVO> selectUserList(UserQueryDTO userQueryDTO);
}
