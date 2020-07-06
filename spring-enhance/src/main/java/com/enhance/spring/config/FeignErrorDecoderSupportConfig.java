package com.enhance.spring.config;

import com.enhance.spring.controller.feign.ExceptionErrorDecoder;
import com.enhance.spring.helper.ApplicationContextHelper;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

/**
 * <p>
 * FeignErrorDecoderSupportConfig 使用场景是指定某个feignClient使用，
 * 如果要全局feign都使用可以使用在启动类上加@EnableFeignErrorDecoder
 *
 * 例子：@FeignClient(...,configuration = FeignErrorDecoderSupportConfig.class,...)
 *
 * </p>
 *
 * @author gongliangjun 2020/06/18 3:44 PM
 */
public class FeignErrorDecoderSupportConfig {

  @Bean
  ErrorDecoder springEnhanceExceptionErrorDecoder1() {
    ExceptionErrorDecoder bean =
        ApplicationContextHelper.getContext().getBean(ExceptionErrorDecoder.class);
    if (bean != null) {
      return bean;
    } else {
      return new ExceptionErrorDecoder();
    }
  }
}
