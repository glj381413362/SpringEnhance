package com.enhance.logplugin.demo.filter;

import com.common.tools.util.Builder;
import com.enhance.logplugin.demo.controller.dto.OtherResult;
import com.enhance.logplugin.demo.controller.dto.OtherResult1;
import com.enhance.spring.config.properties.ResponseCodeProperty;
import com.enhance.spring.constants.EnhanceConstants;
import com.enhance.spring.controller.ResponseConvertService;
import com.enhance.spring.controller.data.Res;
import com.enhance.spring.controller.data.Result;
import org.springframework.stereotype.Component;

/**
 * <p>
 *
 * </p>
 *
 * @author gongliangjun 2019/07/01 11:18
 */
@Component
public class Other2ResponseConvertService implements ResponseConvertService<OtherResult1, Result> {

  @Override
  public int order() {
    return 3;
  }

  @Override
  public boolean supports(Object result) {
    return result instanceof OtherResult1;
  }


  @Override
  public Result convert(ResponseCodeProperty responseCodeProperty, OtherResult1 result) {
    String status = EnhanceConstants.SUCCESS;
    String code1 = result.getCode();
    if ("0".equals(code1)) {
      status = EnhanceConstants.FAILED;
    }
    int code = responseCodeProperty.getCode(status, 200);
    String msg = responseCodeProperty.getMsg(status, EnhanceConstants.SUCCESS);
    return Builder.of(Res::new)
        .with(Res::setStatus, status)
        .with(Res::setCode, code)
        .with(Res::setMsg, msg)
        .with(Res::setResponseBody, result.getData())
        .build();
  }
}
