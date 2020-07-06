package com.enhance.spring.controller.advice;

import com.common.tools.util.Builder;
import com.enhance.spring.config.properties.ResponseCodeProperty;
import com.enhance.spring.constants.EnhanceConstants;
import com.enhance.spring.controller.ResponseConvertService;
import com.enhance.spring.controller.data.Res;
import com.enhance.spring.controller.data.Result;

/**
 * 默认响应转化服务
 *
 * @author gongliangjun 2019/07/01 11:18
 */
public class DefaultResponseConvertService implements ResponseConvertService<Object, Result> {

  @Override
  public int order() {
    return Integer.MAX_VALUE;
  }

  @Override
  public boolean supports(Object result) {
    return true;
  }


  @Override
  public Result convert(ResponseCodeProperty responseCodeProperty, Object source) {
    String status = EnhanceConstants.SUCCESS;
    int code = responseCodeProperty.getCode(status, 200);
    String msg = responseCodeProperty.getMsg(status, EnhanceConstants.SUCCESS);
    return Builder.of(Res::new)
        .with(Res::setStatus, status)
        .with(Res::setCode, code)
        .with(Res::setMsg, msg)
        .with(Res::setResponseBody, source)
        .build();
  }
}
