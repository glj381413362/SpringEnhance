package com.enhance.logplugin.demo.exception;

import com.common.tools.util.exception.IAssertException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 异常枚举类 用法： 1、USER_NOT_FOUND.assertNotNull(obj);
 * 2、USER_NOT_FOUND.assertNotNull(obj,"用户名:{}","bob");
 *
 * @author gongliangjun 2020/06/12 5:25 PM
 */
@Getter
@AllArgsConstructor
public enum UserExceptionAssert implements IAssertException {

  /** 业务异常 */
  USER_ALREADY_EXISTS(8000, "user.already.exists", "用户已经存在"),
  /** 不存在的用户 */
  USER_NOT_FOUND(8001, "user.not.found", "用户不存在");

  /** 返回码 */
  private int code;
  /** 返回状态 */
  private String status;
  /** 返回消息 */
  private String message;
}
