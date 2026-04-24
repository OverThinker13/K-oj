package cn.overthinker.system.controller;

import cn.overthinker.common.core.domain.R;
import cn.overthinker.system.domain.LoginDTO;
import cn.overthinker.system.domain.SysUser;
import cn.overthinker.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequestMapping("/sysUser")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    // 登录成功还是失败 (true || false || int code)
    // 如果失败需要给出失败的原因 (String msg)

    // 接口地址：/sysUser/login
    @PostMapping("/login")
    public R<Void> login(@RequestBody LoginDTO loginDTO){
        return sysUserService.login(loginDTO.getUserAccount(),loginDTO.getPassword());
    }

    // 管理员的增删改查



    // 新增
    @PostMapping("/add")
    public  add(@RequestBody ){

    }
}
