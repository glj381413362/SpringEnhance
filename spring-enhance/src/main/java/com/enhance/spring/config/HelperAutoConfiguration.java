package com.enhance.spring.config;

import com.enhance.spring.helper.ApplicationContextHelper;
import com.enhance.spring.helper.TransactionalHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 帮助类自动注入
 *
 * @author gongliangjun 2020/06/13 5:27 PM
 */
@Configuration
public class HelperAutoConfiguration {

  @Bean
  public TransactionalHelper transactionalHelper() {
    return new TransactionalHelper();
  }
  @Bean
  public ApplicationContextHelper applicationContextHelper() {
    return new ApplicationContextHelper();
  }
}
