package com.enhance.spring.controller.data;

import com.enhance.spring.constants.EnhanceConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 部分成功响应实体类
 *
 * @author gongliangjun 2020/06/14 4:51 PM
 */
@Data
public class PartialSuccesResponse<T> extends Result {
  @ApiModelProperty(value = "部分失败的数据,部分失败时才会有数据")
  private T failureData;

  public PartialSuccesResponse() {
    setStatus(EnhanceConstants.PARTIAL_SUCCESS);
  }

}
