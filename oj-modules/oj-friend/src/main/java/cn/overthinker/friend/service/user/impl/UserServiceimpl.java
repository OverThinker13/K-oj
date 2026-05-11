package cn.overthinker.friend.service.user.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.overthinker.common.core.constants.CacheConstants;
import cn.overthinker.common.core.constants.Constants;
import cn.overthinker.common.core.enums.ResultCode;
import cn.overthinker.common.core.enums.UserIdentity;
import cn.overthinker.common.core.enums.UserStatus;
import cn.overthinker.common.message.service.AliSmsService;
import cn.overthinker.common.redis.service.RedisService;
import cn.overthinker.common.security.exception.ServiceException;
import cn.overthinker.common.security.service.TokenService;
import cn.overthinker.friend.domain.user.User;
import cn.overthinker.friend.domain.user.dto.UserDTO;
import cn.overthinker.friend.mapper.UserMapper;
import cn.overthinker.friend.service.user.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
    private TokenService tokenService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserMapper userMapper;

    @Value("${sms.code-expiration:5}")
    private Long phoneCodeExpiration;

    @Value("${sms.send-limit:3}")
    private Integer sendLimit;

    @Value("${sms.is-send:false}")
    private boolean isSend;  //开关打开就是true；关闭就是false

    @Value("${jwt.secret}")
    private String secret;


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


        //这里由阿里云帮我们创建四位数验证码,通过isSend切换是否为正式还是生产场景，避免浪费消耗资源
        String code;
        if (isSend) {
            code = aliSmsService.sendSmsCode(userDTO.getPhone());
            if (code == null) {
                throw new ServiceException(ResultCode.FAILED_SEND_CODE);
            }
        } else {
            code = Constants.DEFAULT_CODE;
            log.info("短信功能已关闭，使用默认验证码：{}", code);
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

    @Override
    public String codeLogin(String phone, String code) {
        //先进行验证码的比对
        checkCode(phone, code);
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (user == null) {
            //如果为空就是新用户，先执行注册逻辑
            user = new User();
            user.setPhone(phone);
            user.setStatus(UserStatus.Normal.getValue());
            userMapper.insert(user);
        }

        //执行登录逻辑，生成token传给前端
        return tokenService.createToken(user.getUserId(), secret, UserIdentity.ORDINARY.getValue(), user.getNickName());
    }

    private void checkCode(String phone, String code) {
        String phoneCodeKey = getPhoneCodeKey(phone);
        String cacheCode = redisService.getCacheObject(phoneCodeKey, String.class);
        //善后用户拿到验证码迟迟不登陆导致验证码过期的情况
        if (StrUtil.isEmpty(cacheCode)) {
            throw new ServiceException(ResultCode.FAILED_INVALID_CODE);
        }
        //验证码错误
        if (!cacheCode.equals(code)) {
            throw new ServiceException(ResultCode.FAILED_ERROR_CODE);
        }
        //验证码比对成功
        //删除原有redis储存的验证码
        redisService.deleteObject(phoneCodeKey);
    }


    private String getPhoneCodeKey(String phone) {
        return CacheConstants.PHONE_CODE_KEY + phone;
    }

    private String getCodeTimeKey(String phone) {
        return CacheConstants.CODE_TIME_KEY + phone;
    }
}
