package cn.overthinker.system.controller.user;

import cn.overthinker.common.core.controller.BaseController;
import cn.overthinker.common.core.domain.R;
import cn.overthinker.common.core.domain.TableDataInfo;
import cn.overthinker.system.domain.user.dto.UserDTO;
import cn.overthinker.system.domain.user.dto.UserQueryDTO;
import cn.overthinker.system.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    //获取用户列表
    @GetMapping("/list")
    public TableDataInfo list(UserQueryDTO userQueryDTO) {
        return getTableDataInfo(userService.list(userQueryDTO));
    }

    // 更新用户状态信息
    @PutMapping("/updateStatus")
    // todo 拉黑：限制用户操作 解禁：放开对于用户限制
    public R<Void> updateStatus(@RequestBody UserDTO userDTO) {
        return toR(userService.updateStatus(userDTO));
    }
}
