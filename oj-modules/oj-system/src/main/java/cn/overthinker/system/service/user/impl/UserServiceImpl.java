package cn.overthinker.system.service.user.impl;

import cn.overthinker.common.core.domain.TableDataInfo;
import cn.overthinker.common.core.enums.ResultCode;
import cn.overthinker.common.security.exception.ServiceException;
import cn.overthinker.system.domain.user.User;
import cn.overthinker.system.domain.user.dto.UserDTO;
import cn.overthinker.system.domain.user.dto.UserQueryDTO;
import cn.overthinker.system.domain.user.vo.UserVO;
import cn.overthinker.system.mapper.user.UserMapper;
import cn.overthinker.system.service.user.UserService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<UserVO> list(UserQueryDTO userQueryDTO) {
        PageHelper.startPage(userQueryDTO.getPageNum(), userQueryDTO.getPageSize());
        return userMapper.selectUserList(userQueryDTO);
    }

    @Override
    public int updateStatus(UserDTO userDTO) {
        User user = userMapper.selectById(userDTO.getUserId());
        if (user == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        user.setStatus(userDTO.getStatus());
        return userMapper.updateById(user);
    }
}


