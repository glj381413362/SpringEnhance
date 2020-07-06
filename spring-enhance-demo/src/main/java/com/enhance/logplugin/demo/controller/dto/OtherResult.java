package com.enhance.logplugin.demo.controller.dto;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author gongliangjun 2019/07/01 11:18
 */
@Data
public class OtherResult<T> {

  String code;
  String msg;
  T data;

}
