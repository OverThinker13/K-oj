package cn.overthinker.friend.test;


import cn.overthinker.common.core.controller.BaseController;
import cn.overthinker.common.core.domain.R;
import cn.overthinker.common.core.enums.ResultCode;
import cn.overthinker.common.message.service.AliSmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController extends BaseController {

    @Autowired
    private AliSmsService aliSmsService;

    @GetMapping("/sendCode")
    public R<String> sendCode(String phone) {
        log.info("验证码发送测试");
        String bizId = aliSmsService.sendSmsCode(phone);

        // 如果 bizId 不为空，说明发送成功，封装进 R 对象返回
        return bizId != null ? R.ok(bizId) : R.fail(ResultCode.FAILED);
    }
}
