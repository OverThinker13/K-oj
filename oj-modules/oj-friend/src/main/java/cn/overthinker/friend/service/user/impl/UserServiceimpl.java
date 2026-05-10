package cn.overthinker.friend.service.user.impl;

import cn.overthinker.common.core.enums.ResultCode;
import cn.overthinker.common.message.service.AliSmsService;
import cn.overthinker.common.security.exception.ServiceException;
import cn.overthinker.friend.domain.user.dto.UserDTO;
import cn.overthinker.friend.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UserServiceimpl implements UserService {

    @Autowired
    private AliSmsService aliSmsService; // 注入你刚写好的服务

    //检验手机号格式是否合法
    public static boolean checkPhone(String phone) {
        Pattern regex = Pattern.compile("^1[2|3|4|5|6|7|8|9][0-9]\\d{8}$");
        Matcher m = regex.matcher(phone);
        return m.matches();
    }

    @Override
    public void sendCode(UserDTO userDTO) {
        if (!checkPhone(userDTO.getPhone())) {
            throw new ServiceException(ResultCode.FAILED_USER_PHONE);
        }
        String code = aliSmsService.sendSmsCode(userDTO.getPhone());

//        if (code == null) {
//            throw new ServiceException(ResultCode.FAILED, "短信发送失败");
//        }

        // 3. 为了测试方便，我们可以把 code 打印出来或者返回
        log.info("短信发送成功，code: {}", code);
    }
}
