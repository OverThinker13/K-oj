package cn.overthinker.common.message.config;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dypnsapi20170525.*;
import darabonba.core.client.ClientOverrideConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AliSmsConfig {

    @Value("${sms.aliyun.accessKeyId:}")
    private String accessKeyId;

    @Value("${sms.aliyun.accessKeySecret:}")
    private String accessKeySecret;

    @Value("${sms.aliyun.endpoint:dypnsapi.aliyuncs.com}")
    private String endpoint;

    @Value("${sms.aliyun.region:cn-shenzhen}")
    private String region;

    @Bean("aliClient")
    public AsyncClient aliClient() {
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(accessKeyId)
                .accessKeySecret(accessKeySecret)
                .build());

        return AsyncClient.builder()
                .region(region)
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride(endpoint)
                                .setConnectTimeout(Duration.ofSeconds(30))
                )
                .build();
    }
}