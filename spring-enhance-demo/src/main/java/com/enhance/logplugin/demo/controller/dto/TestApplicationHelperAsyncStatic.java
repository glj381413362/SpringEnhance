package com.enhance.logplugin.demo.controller.dto;

import com.enhance.logplugin.demo.filter.UserInfoService;
import com.enhance.spring.helper.ApplicationContextHelper;

/** @author gongliangjun 2019/07/01 11:18 */
public class TestApplicationHelperAsyncStatic {

  private static UserInfoService userInfoService;

  static {
    ApplicationContextHelper
        .asyncStaticSetter(UserInfoService.class, TestApplicationHelperAsyncStatic.class,"setUserInfoService");
  }

  public static void setUserInfoService(UserInfoService userInfoService) {
    TestApplicationHelperAsyncStatic.userInfoService = userInfoService;
  }

  public static boolean isNull() {
    return null == userInfoService;
  }
}
