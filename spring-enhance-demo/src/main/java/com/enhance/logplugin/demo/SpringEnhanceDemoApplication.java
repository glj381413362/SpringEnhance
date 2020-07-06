package com.enhance.logplugin.demo;

import com.enhance.spring.annotations.EnableExceptionHandler;
import com.enhance.spring.annotations.EnableUnifiedResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
// @EnableZuulProxy
@EnableUnifiedResponse
@EnableExceptionHandler
public class SpringEnhanceDemoApplication {

  public static void main(String[] args) {
    try {
      SpringApplication.run(SpringEnhanceDemoApplication.class, args);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
