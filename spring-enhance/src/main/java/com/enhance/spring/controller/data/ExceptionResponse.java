package com.enhance.spring.controller.data;

import com.common.tools.util.exception.AssertException;
import com.common.tools.util.exception.BaseException;
import com.common.tools.util.exception.IException;
import com.common.tools.util.exception.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.ArrayUtils;

/**
 * 异常响应实体类
 *
 * @author wuguokai
 */
public class ExceptionResponse extends Result {
  private String userMsg;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String exception;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String originalException;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String[] trace;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String[] throwable;

  public ExceptionResponse(BaseException baseException) {
    setRes(CommonRes.FAILED);
    setStatus(baseException.getStatus());
    setCode(500);
    setMsg(baseException.getMessage());
  }

  public ExceptionResponse(Exception e) {
    setRes(CommonRes.FAILED);
    setStatus("error");
    setCode(500);
    setMsg(e.getMessage());
  }

  public ExceptionResponse(AssertException e) {
    IException exception = e.getException();
    setRes(CommonRes.FAILED);
    setStatus(exception.getStatus());
    setCode(exception.getCode());
    setMsg(e.getMessage());
  }

  public ExceptionResponse(Exception e, Status staus) {
    setRes(CommonRes.FAILED);
    setStatus(staus.getStatus());
    setCode(500);
    setMsg(e.getMessage());
  }

  public String getOriginalException() {
    return originalException;
  }

  public void setOriginalException(String originalException) {
    this.originalException = originalException;
  }

  public String[] getTrace() {
    return trace;
  }

  public ExceptionResponse setTrace(StackTraceElement[] trace) {
    this.trace = ArrayUtils.toStringArray(trace);
    return this;
  }

  public String[] getThrowable() {
    return throwable;
  }

  public ExceptionResponse setThrowable(String message, StackTraceElement[] trace) {
    this.throwable = ArrayUtils.insert(0, ArrayUtils.toStringArray(trace), message);
    return this;
  }

  public String getException() {
    return exception;
  }

  public void setException(String exception) {
    this.exception = exception;
  }

  public String getUserMsg() {
    return userMsg;
  }

  public void setUserMsg(String userMsg) {
    this.userMsg = userMsg;
  }

}
