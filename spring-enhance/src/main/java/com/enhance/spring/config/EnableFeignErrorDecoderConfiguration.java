package com.enhance.spring.config;

import org.springframework.context.annotation.ComponentScan;

/**
 * 统一请求响应配置
 *
 * @author gongliangjun 2019/07/01 11:18
 */
@ComponentScan({"com.enhance.spring.controller.feign"})
public class EnableFeignErrorDecoderConfiguration {}
