package com.enhance.spring.controller.data;

import com.common.tools.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 统一接口响应DTO
 *
 * @author gongliangjun 2020/06/20 5:24 PM
 */
@Slf4j
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result<T> implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonIgnore private CommonRes res = CommonRes.SUCCESS;

  @ApiModelProperty(value = "响应状态,根据状态获取对应的code,及错误信息message")
  private String status;

  @ApiModelProperty(value = "响应编码,一般不自己设置，通过status从配置表获取")
  private Integer code;

  /** 返回信息 */
  @ApiModelProperty(value = "响应消息,一般不自己设置，通过status从配置表获取")
  private String message;

  @ApiModelProperty(value = "响应数据")
  private T responseBody;

  /**
   * 是否成功 在返回json字符串时也会带有success字段,可用于标识此次请求成功还是失败
   *
   * @return boolean
   * @author 龚梁钧 2019-06-14 15:20
   */
  public boolean isSuccess() {
    return CommonRes.SUCCESS.equals(this.res);
  }

  /**
   * 是否失败 在返回json字符串时也会带有failed字段,可用于标识此次请求成功还是失败
   *
   * @return boolean
   * @author 龚梁钧 2019-06-14 15:20
   */
  public boolean isFailed() {
    return CommonRes.FAILED.equals(this.res);
  }

  /**
   * 用于判断方法返回是否失败，可带日志信息
   *
   * @param msg
   * @param obj
   * @author gongliangjun 2020-06-20 5:27 PM
   * @return boolean
   */
  public boolean isFailed(String msg, String... obj) {
    boolean equals = CommonRes.FAILED.equals(this.res);
    if (equals) {
      log.info(StringUtil.strFormat(msg, obj) + " :[{}],原因:[{}]", equals, this.getMessage());
    } else {
      log.info(StringUtil.strFormat(msg, obj) + " :[{}]", equals);
    }
    return equals;
  }

  public String getMsg() {
    return this.message;
  }

  @JsonIgnore
  public void setMsg(String message) {
    this.message = message;
  }
}
