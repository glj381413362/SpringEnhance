package com.enhance.logplugin.demo.controller.dto;

import com.enhance.logplugin.demo.filter.UserInfoService;
import com.enhance.spring.helper.ApplicationContextHelper;

/** @author gongliangjun 2019/07/01 11:18 */
public class TestApplicationHelper {



  private UserInfoService userInfoService;

  public void setUserInfoService(UserInfoService userInfoService) {
    this.userInfoService = userInfoService;
  }

  public boolean isNull() {
    return null == this.userInfoService;
  }
}
