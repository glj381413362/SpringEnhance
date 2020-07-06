package com.enhance.spring.config.properties;

import com.common.tools.util.exception.BusinessExceptionAssert;
import com.enhance.spring.controller.GetLanguageService;
import com.enhance.spring.message.Message;
import com.enhance.spring.message.MessageHelper;
import java.util.Locale;
import java.util.Properties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.util.Assert;
/**
 * 响应code message 配置类
 *
 * @author gongliangjun 2020/06/20 5:38 PM
 */
@Data
@Slf4j
@AllArgsConstructor
public class ResponseCodeProperty {
  private Properties responseCodeProperties;
  private GetLanguageService getLanguageService;
  private SpringResponseProperty springResponseProperty;

  private ResponseCodeProperty() {
    BusinessExceptionAssert.ILLEGAL_OPERATION_EXCEPTION.throwE();
  }

  public ResponseCodeProperty(GetLanguageService getLanguageService) {
    this.getLanguageService = getLanguageService;
  }

  public int getCode(String status, int defaultValue) {
    Assert.notNull(status, "status 为空");
    try {
      String code = status.toLowerCase();
      if (springResponseProperty.isMultilingual()) {
        Locale language = getLanguageService.getLanguage();
        if (language == null) {
          language = LocaleUtils.toLocale("zh_CN");
        }
        Message message = MessageHelper.getMessage(code, null, language);
        if (message == null) {
          log.warn("未维护response的status[{}]对应的code,使用默认值[{}]", status, defaultValue);
          return defaultValue;
        }
        return Integer.parseInt(message.getDesc());
      } else {
        String property =
            responseCodeProperties.getProperty(code.toLowerCase(), String.valueOf(defaultValue));
        return Integer.parseInt(property);
      }
    } catch (Exception e) {
      log.warn("ResponseCodeProperty getCode error {} ", e);
    }
    return defaultValue;
  }

  public String getMsg(String status, String defaultValue) {
    Assert.notNull(status, "status 为空");
    try {
      String code = status.toLowerCase();
      if (springResponseProperty.isMultilingual()) {
        Locale language = getLanguageService.getLanguage();
        if (language == null) {
          language = LocaleUtils.toLocale("zh_CN");
        }
        Message message = MessageHelper.getMessage(code + ".msg", null, language);
        if (message == null) {
          log.warn("未维护response的status[{}]对应的msg,使用默认值[{}]", status, defaultValue);
          return defaultValue;
        }
        return message.getDesc();
      } else {
        String property = responseCodeProperties.getProperty(code + ".msg", defaultValue);
        return property;
      }
    } catch (Exception e) {
      log.warn("ResponseCodeProperty getMsg error {} ", e);
    }
    return defaultValue;
  }
}
