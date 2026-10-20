package cn.marlboro.ai.superaiagent.config;

import cn.marlboro.ai.superaiagent.properties.AliOssProperties;
import cn.marlboro.ai.superaiagent.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AliOssConfig {

    @Bean
    public AliOssUtil AliOssConfiguration(AliOssProperties aliOssProperties){
        log.info("开始生成阿里云对象");
        return new AliOssUtil(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName()
        );
    }
}
