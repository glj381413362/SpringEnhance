package com.enhance.spring.annotations;

import com.enhance.spring.config.EnableUnifiedExceptionHandlerConfiguration;
import com.enhance.spring.config.EnableUnifiedResponseConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * <p>
 *  开启统一异常处理
 * </p>
 *
 * @author gongliangjun 2019/07/01 11:18
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({EnableUnifiedExceptionHandlerConfiguration.class})
public @interface EnableExceptionHandler {

}
