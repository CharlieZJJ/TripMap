package com.ecnu.tripmap.config;

import com.ecnu.tripmap.utils.QCloudCosUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QCloudCosUtilsConfig {
    @Bean
    @ConfigurationProperties(prefix = "tencent.cos")
    public QCloudCosUtils qcloudCosUtils() {
        return new QCloudCosUtils();
    }
}
