package cn.overthinker.common.message.service;

import com.aliyun.sdk.service.dypnsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dypnsapi20170525.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 阿里云短信验证码服务（基于号码认证服务 Dypnsapi）
 */
@Service
@Slf4j
public class AliSmsService {

    @Autowired
    private AsyncClient aliClient;

    @Value("${sms.aliyun.sign-name}")
    private String signName;

    @Value("${sms.aliyun.template-code}")
    private String templateCode;

    /**
     * 发送短信验证码
     * * @param phone 目标手机号
     *
     * @return bizId 用于后续校验的业务ID，发送失败返回 null
     */
    public String sendSmsCode(String phone) {
        if (!StringUtils.hasText(phone)) {
            log.warn("发送短信失败：手机号为空");
            return null;
        }

        try {
            // 构造请求参数
            SendSmsVerifyCodeRequest request = SendSmsVerifyCodeRequest.builder()
                    .phoneNumber(phone)
                    .signName(signName)
                    .templateCode(templateCode)
                    .templateParam("{\"code\":\"##code##\",\"min\":\"5\"}")
                    .interval(60L)      // 60秒内不可重复发送
                    .validTime(300L)    // 验证码5分钟内有效
                    .build();

            // 异步调用并获取结果
            CompletableFuture<SendSmsVerifyCodeResponse> future = aliClient.sendSmsVerifyCode(request);
            // 建议设置超时时间，防止线程死锁
            SendSmsVerifyCodeResponse response = future.get(10, TimeUnit.SECONDS);

            // 校验响应结果
            if (response.getBody() != null && "OK".equalsIgnoreCase(response.getBody().getCode())) {
                // 特别注意：Dypnsapi 的 BizId 嵌套在 Model 对象中
                if (response.getBody().getModel() != null) {
                    String bizId = response.getBody().getModel().getBizId();
                    log.info("短信验证码发送成功，phone: {}, bizId: {}", phone, bizId);
                    return bizId;
                }
            }

            String errorMsg = (response.getBody() != null) ? response.getBody().getMessage() : "SDK响应空";
            log.error("短信发送失败，原因: {}", errorMsg);
            return null;

        } catch (Exception e) {
            log.error("短信发送过程发生异常, phone: {}", phone, e);
            return null;
        }
    }

    /**
     * 校验用户输入的验证码是否正确
     *
     * @param phone         手机号
     * @param userInputCode 用户填写的验证码
     * @param bizId         发送成功时返回的业务ID
     * @return 校验是否通过
     */
    public boolean verifySmsCode(String phone, String userInputCode, String bizId) {
        if (!StringUtils.hasText(userInputCode) || !StringUtils.hasText(bizId)) {
            log.warn("验证码校验失败：参数不完整");
            return false;
        }

        try {
            // 构造校验请求
            CheckSmsVerifyCodeRequest request = CheckSmsVerifyCodeRequest.builder()
                    .phoneNumber(phone)
                    .verifyCode(userInputCode)
                    .outId(bizId) // 核心：发送时的 bizId 对应这里的 outId
                    .build();

            CompletableFuture<CheckSmsVerifyCodeResponse> future = aliClient.checkSmsVerifyCode(request);
            CheckSmsVerifyCodeResponse response = future.get(10, TimeUnit.SECONDS);

            boolean isSuccess = response.getBody() != null && "OK".equalsIgnoreCase(response.getBody().getCode());

            if (isSuccess) {
                log.info("验证码校验通过, phone: {}", phone);
            } else {
                String errorMsg = (response.getBody() != null) ? response.getBody().getMessage() : "校验失败";
                log.warn("验证码校验未通过, phone: {}, 原因: {}", phone, errorMsg);
            }

            return isSuccess;

        } catch (Exception e) {
            log.error("验证码校验异常, phone: {}", phone, e);
            return false;
        }
    }
}