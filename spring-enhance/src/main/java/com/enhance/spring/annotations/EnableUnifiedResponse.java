package com.enhance.spring.annotations;

import com.enhance.spring.config.EnableUnifiedResponseConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * 开启接口统一返回结果
 *
 * @author gongliangjun 2019/07/01 11:18
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({EnableUnifiedResponseConfiguration.class})
public @interface EnableUnifiedResponse {}
