package com.enhance.spring.config;

import com.common.tools.util.exception.BusinessExceptionAssert;
import com.enhance.spring.config.properties.ResponseCodeProperty;
import com.enhance.spring.config.properties.SpringResponseProperty;
import com.enhance.spring.controller.GetLanguageService;
import com.enhance.spring.controller.advice.ResponseBodyHandler;
import java.io.IOException;
import java.util.Properties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * 自动注入
 *
 * @author gongliangjun 2020/06/13 5:27 PM
 */
@Configuration
@ComponentScan({"com.enhance.spring.controller.advice"})
public class SpringEnhanceAutoConfiguration implements ApplicationContextAware {
  private ApplicationContext applicationContext;

  @Bean
  public Properties responseCodeProperties() throws IOException {
    PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
    propertiesFactoryBean.setLocation(new ClassPathResource("responseCode.properties"));
    propertiesFactoryBean.setFileEncoding("UTF-8");
    propertiesFactoryBean.afterPropertiesSet();
    return propertiesFactoryBean.getObject();
  }

  @Bean
  @ConditionalOnBean(ResponseBodyHandler.class)
  @ConfigurationProperties(prefix = SpringResponseProperty.SPING_ENHANCE_PREFIX)
  public SpringResponseProperty springResponseProperty() {
    return new SpringResponseProperty();
  }

  @Bean
  @ConditionalOnBean(name = {"responseCodeProperties", "springResponseProperty"})
  public ResponseCodeProperty responseCodeProperty(
      Properties responseCodeProperties, SpringResponseProperty springResponseProperty) {
    GetLanguageService getLanguageService = null;
    if (springResponseProperty.isMultilingual()) {
      try {
        getLanguageService = applicationContext.getBean(GetLanguageService.class);
      } catch (BeansException e) {
        BusinessExceptionAssert.BUSINESS_EXCEPTION.throwE(
            "开启了使用多语言，但是未实现接口GetLanguageService,所以无法获取使用语言");
      }
    }
    ResponseCodeProperty responseCodeProperty =
        new ResponseCodeProperty(
            responseCodeProperties, getLanguageService, springResponseProperty);
    return responseCodeProperty;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
