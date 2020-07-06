package com.enhance.spring.controller;

import com.enhance.spring.config.properties.ResponseCodeProperty;

/**
 * 响应转化服务
 *
 * @author gongliangjun 2019/07/01 11:18
 */
public interface ResponseConvertService<S, T> {

  int order();

  boolean supports(Object result);

  T convert(ResponseCodeProperty responseCodeProperty,S result);
}
