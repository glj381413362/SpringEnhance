package com.enhance.logplugin.demo.exception;

import com.common.tools.util.exception.IAssertException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 *
 * </p>
 *
 * @author gongliangjun 2019/07/01 11:18
 */
@Getter
@AllArgsConstructor
public enum OrderExceptionAssert implements IAssertException {
  /** 业务异常 */
  ORDER_ALREADY_EXISTS(7000, "order.already.exists", "订单已经存在"),
  /** 不存在的用户 */
  ORDER_NOT_FOUND(6001, "order.not.found", "订单不存在");

  /** 返回码 */
  private int code;
  /** 返回状态 */
  private String status;
  /** 返回消息 */
  private String message;
}
