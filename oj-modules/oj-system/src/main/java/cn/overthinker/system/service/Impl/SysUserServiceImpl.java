package cn.overthinker.system.service.Impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.overthinker.common.core.domain.LoginUser;
import cn.overthinker.common.core.domain.R;
import cn.overthinker.common.core.domain.vo.LoginUserVO;
import cn.overthinker.common.core.enums.ResultCode;
import cn.overthinker.common.core.enums.UserIdentity;
import cn.overthinker.common.security.exception.ServiceException;
import cn.overthinker.common.security.service.TokenService;
import cn.overthinker.system.domain.SysUser;
import cn.overthinker.system.domain.dto.SysUserSaveDTO;
import cn.overthinker.system.mapper.SysUserMapper;
import cn.overthinker.system.service.SysUserService;
import cn.overthinker.system.utils.BCryptUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RefreshScope
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    // 从配置中读取secret信息（配置在Nacos统一管理）
    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private TokenService tokenService;


    @Override
    // 需要考虑维护性、性能、安全
    public R<String> login(String userAccount, String password) {


        SysUser sysUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .select(SysUser::getUserId, SysUser::getPassword, SysUser::getNickName)
                .eq(SysUser::getUserAccount, userAccount));


        if (sysUser == null) {
//            loginResult.setCode(ResultCode.FAILED_USER_NOT_EXISTS.getCode());
//            loginResult.setMsg(ResultCode.FAILED_USER_NOT_EXISTS.getMsg());
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        if (BCryptUtils.matchesPassword(password, sysUser.getPassword())) {
//            loginResult.setCode(ResultCode.SUCCESS.getCode());
//            loginResult.setMsg(ResultCode.SUCCESS.getMsg());
            String token = tokenService.createToken(sysUser.getUserId(), secret, UserIdentity.ADMIN.getValue(), sysUser.getNickName());
            return R.ok(token);
        }
//        loginResult.setCode(ResultCode.FAILED_LOGIN.getCode());
//        loginResult.setMsg(ResultCode.FAILED_LOGIN.getMsg());
//        return loginResult;
        return R.fail(ResultCode.FAILED_LOGIN);


        // 全局异常处理，日志框架
        //  1.增加全局异常处理  2. 当捕获异常时。记录相关日志，作为问题排查线索
    }

    @Override
    public int add(SysUserSaveDTO sysUserSaveDTO) {
//        checkParams(sysUserSaveDTO);
        //将DTO转为实体
        List<SysUser> sysUserList = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserAccount, sysUserSaveDTO.getUserAccount()));
        // 如果不为空返回true，说明用户已经存在，我们需要返回错误
        if (CollectionUtil.isNotEmpty(sysUserList)) {
            // 提供自定义异常 是个公共异常
            throw new ServiceException(ResultCode.AILED_USER_EXISTS);
        }
        SysUser sysUser = new SysUser();
        sysUser.setUserAccount(sysUserSaveDTO.getUserAccount());
        sysUser.setPassword(BCryptUtils.encryptPassword(sysUserSaveDTO.getPassword()));
        return sysUserMapper.insert(sysUser);

    }

    @Override
    public R<LoginUserVO> info(String token) {
        LoginUser loginUser = tokenService.getLoginUser(token, secret);
        if (loginUser == null) {
            return R.fail();
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        loginUserVO.setNickName(loginUser.getNickName());
        return R.ok(loginUserVO);
    }


//    private void checkParams(SysUserSaveDTO sysUserSaveDTO) {
//        String password = sysUserSaveDTO.getPassword();
//        if (password == null) {
//            throw new ServiceException();
//        }
//    }

}
