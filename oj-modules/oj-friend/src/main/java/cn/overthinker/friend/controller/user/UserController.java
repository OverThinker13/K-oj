package cn.overthinker.friend.controller.user;

import cn.overthinker.common.core.constants.HttpConstants;
import cn.overthinker.common.core.controller.BaseController;
import cn.overthinker.common.core.domain.R;
import cn.overthinker.common.core.domain.vo.LoginUserVO;
import cn.overthinker.friend.domain.user.dto.UserDTO;
import cn.overthinker.friend.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    //获取验证码
    @PostMapping("/sendCode")
    public R<Void> sendCode(@RequestBody UserDTO userDTO) {
        return toR(userService.sendCode(userDTO));
    }

    //登录/注册请求
    @PostMapping("/code/login")
    public R<String> codeLogin(@RequestBody UserDTO userDTO) {
        return R.ok(userService.codeLogin(userDTO.getPhone(), userDTO.getCode()));
    }

    //退出登录功能
    @DeleteMapping("/logout")
    public R<Void> logout(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return toR(userService.logout(token));
    }

    //获取用户信息
    @GetMapping("/info")
    public R<LoginUserVO> info(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return userService.info(token);
    }
}
