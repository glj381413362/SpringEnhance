package com.enhance.spring.controller.data;

import com.enhance.spring.constants.EnhanceConstants;
import lombok.Data;

/**
 * 分页结果响应实体类
 *
 * @author gongliangjun 2020/06/14 4:51 PM
 */
@Data
public class PageResponse<T> extends Result {

  private Integer totalPages;
  private Long totalElements;
  private Integer numberOfElements;
  private Integer size;
  private Integer number;

  public PageResponse() {
        setStatus(EnhanceConstants.SUCCESS);
  }
}
