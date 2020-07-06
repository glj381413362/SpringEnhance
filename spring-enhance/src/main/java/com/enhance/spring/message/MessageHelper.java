package com.enhance.spring.message;

import java.util.Locale;

/** 国际化消息工具类. */
public final class MessageHelper {

  private static final MultiReloadableResourceBundleMessageSource MULTI_MESSAGE_SOURCE;

  static {
    MULTI_MESSAGE_SOURCE = new MultiReloadableResourceBundleMessageSource();
    MULTI_MESSAGE_SOURCE.setBasenames("classpath*:messages/*");
    MULTI_MESSAGE_SOURCE.setDefaultEncoding("UTF-8");
  }

  private MessageHelper() {
    throw new UnsupportedOperationException("Utility class");
  }

  /** 从本地消息文件获取多语言消息 */
  public static String getMessageString(String code, Locale locale) {
    return MULTI_MESSAGE_SOURCE.getMessage(code, null, locale);
  }

  /** 从本地消息文件获取多语言消息 */
  public static Message getMessage(String code, Locale locale) {
    return new Message(code, MULTI_MESSAGE_SOURCE.getMessage(code, null, locale));
  }

  /** 从本地消息文件获取多语言消息 */
  public static String getMessageString(String code, Object[] args, Locale locale) {
    return MULTI_MESSAGE_SOURCE.getMessage(code, args, locale);
  }

  /** 从本地消息文件获取多语言消息 */
  public static Message getMessage(String code, Object[] args, Locale locale) {
    return new Message(code, MULTI_MESSAGE_SOURCE.getMessage(code, args, locale));
  }
}
