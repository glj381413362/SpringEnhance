package com.enhance.spring.config;

import com.enhance.spring.controller.ResponseConvertService;
import com.enhance.spring.controller.advice.DefaultResponseConvertService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 统一请求响应配置
 *
 * @author gongliangjun 2019/07/01 11:18
 */
@ComponentScan({"com.enhance.spring.controller.advice"})
@Configuration
public class EnableUnifiedResponseConfiguration {
  @Bean
  ResponseConvertService defaultResponseConvertService(){
    return new DefaultResponseConvertService();
  }
}
