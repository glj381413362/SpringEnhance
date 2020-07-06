package com.enhance.spring.controller.data;

import com.common.tools.util.exception.BaseException;
import com.enhance.spring.constants.EnhanceConstants;
import lombok.Data;

/**
 * 失败响应实体类
 *
 * @author gongliangjun 2020/06/14 4:51 PM
 */
@Data
public class ErrorResponse extends Result {

  public ErrorResponse() {
    setRes(CommonRes.FAILED);
    setStatus(EnhanceConstants.FAILED);
  }
  public ErrorResponse(BaseException baseException) {
    setRes(CommonRes.FAILED);
    setStatus(baseException.getStatus());
    setCode(500);
    setMsg(baseException.getMessage());
  }
  public ErrorResponse(Exception e) {
    setRes(CommonRes.FAILED);
    setCode(500);
    setMsg(e.getMessage());
  }
}
