package cn.overthinker.friend.service.user.impl;

import cn.overthinker.common.core.constants.CacheConstants;
import cn.overthinker.common.core.enums.ResultCode;
import cn.overthinker.common.message.service.AliSmsService;
import cn.overthinker.common.redis.service.RedisService;
import cn.overthinker.common.security.exception.ServiceException;
import cn.overthinker.friend.domain.user.dto.UserDTO;
import cn.overthinker.friend.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UserServiceimpl implements UserService {

    @Autowired
    private AliSmsService aliSmsService; // 注入阿里云的服务

    @Autowired
    private RedisService redisService;

    @Value("${sms.code-expiration:5}")
    private Long phoneCodeExpiration;

    @Value("${sms.send-limit:3}")
    private Integer sendLimit;

    //检验手机号格式是否合法
    public static boolean checkPhone(String phone) {
        Pattern regex = Pattern.compile("^1[2|3|4|5|6|7|8|9][0-9]\\d{8}$");
        Matcher m = regex.matcher(phone);
        return m.matches();
    }

    @Override
    public boolean sendCode(UserDTO userDTO) {
        if (!checkPhone(userDTO.getPhone())) {
            throw new ServiceException(ResultCode.FAILED_USER_PHONE);
        }
        String phoneCodeKey = getPhoneCodeKey(userDTO.getPhone());
        //判断过期时间
        Long expire = redisService.getExpire(phoneCodeKey, TimeUnit.SECONDS);
        if (expire != null && (phoneCodeExpiration * 60 - expire) < 60) {
            throw new ServiceException(ResultCode.FAILED_FREQUENT);
        }
        //对于每天的验证获取次数是50次，第二天计数清零，重新计数
        //操作这个次数数据频繁、不需要存储、记录的次数是有有效时间的（当天有效）存redis String key: c:t:手机号
        //获取已经请求的次数和50进行比较，如果大于限制抛出异常，如果不大于限制，正常执行后续逻辑，并且将获取计数+1
        String codeTimeKey = getCodeTimeKey(userDTO.getPhone());
        Long sendTimes = redisService.getCacheObject(codeTimeKey, Long.class);
        if (sendTimes != null && sendTimes >= sendLimit) {
            throw new ServiceException(ResultCode.FAILED_TIME_LIMIT);
        }


        //这里由阿里云帮我们创建四位数验证码
        String code = aliSmsService.sendSmsCode(userDTO.getPhone());
        if (code == null) {
            throw new ServiceException(ResultCode.FILED_SEND_CODE);
        }
        // 存储到redis，数据结构：String key：p:c:手机号  value: code
        redisService.setCacheObject(phoneCodeKey, code, phoneCodeExpiration, TimeUnit.MINUTES);
        redisService.increment(codeTimeKey);
        if (sendTimes == null) {
            //说明是当天第一次发起获取验证码请求
            long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(),
                    LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
            redisService.expire(codeTimeKey, seconds, TimeUnit.SECONDS);
        }
        return true;
    }

    private String getPhoneCodeKey(String phone) {
        return CacheConstants.PHONE_CODE_KEY + phone;
    }

    private String getCodeTimeKey(String phone) {
        return CacheConstants.CODE_TIME_KEY + phone;
    }
}
