package cn.overthinker.system.service.Impl;

import cn.overthinker.common.core.domain.R;
import cn.overthinker.common.core.enums.ResultCode;
import cn.overthinker.system.domain.SysUser;
import cn.overthinker.system.mapper.SysUserMapper;
import cn.overthinker.system.service.SysUserService;
import cn.overthinker.system.utils.BCryptUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    // 需要考虑维护性、性能、安全
    public R<Void> login(String userAccount, String password) {


        SysUser sysUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .select(SysUser::getPassword)
                .eq(SysUser::getUserAccount, userAccount));


        if (sysUser == null) {
//            loginResult.setCode(ResultCode.FAILED_USER_NOT_EXISTS.getCode());
//            loginResult.setMsg(ResultCode.FAILED_USER_NOT_EXISTS.getMsg());
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        if (BCryptUtils.matchesPassword(password, sysUser.getPassword())) {
//            loginResult.setCode(ResultCode.SUCCESS.getCode());
//            loginResult.setMsg(ResultCode.SUCCESS.getMsg());
            return R.ok();
        }
//        loginResult.setCode(ResultCode.FAILED_LOGIN.getCode());
//        loginResult.setMsg(ResultCode.FAILED_LOGIN.getMsg());
//        return loginResult;
        return R.fail(ResultCode.FAILED_LOGIN);


        // 全局异常处理，日志框架
        //  1.增加全局异常处理  2. 当捕获异常时。记录相关日志，作为问题排查线索
    }
}
