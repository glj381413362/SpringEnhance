package com.enhance.spring.controller.data;

import com.common.tools.util.Builder;
import com.enhance.spring.constants.EnhanceConstants;
import com.github.pagehelper.PageInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * description
 *
 * @author 龚梁钧 2019/05/16 15:09
 */
public enum CommonRes implements BuildResponse {
  /** 处理成功 */
  SUCCESS("SUCCESS"),
  /** 处理失败 */
  FAILED("FAILED"),
  /** 处理部分成功 */
  PARTIAL_SUCCESS("PARTIAL_SUCCESS");

  private String operation;

  CommonRes(String operation) {
    this.operation = operation;
  }

  private static Result handlerSuccess(Res res) {
    PageInfo pageInfo = res.getPageInfo();
    Object data = res.getResponseBody();
    res.setStatus(EnhanceConstants.SUCCESS);
    // ===============================================================================
    //  分页相关
    // ===============================================================================
    if (data instanceof List) {
      if (null != pageInfo) {
        List list = List.class.cast(data);
        long total = pageInfo.getTotal();
        int size = pageInfo.getSize();
        int totalPages = (int) (total - 1) / size + 1;
        return Builder.of(PageResponse::new)
            .with(PageResponse::setStatus, EnhanceConstants.SUCCESS)
            .with(PageResponse::setCode, 200)
            .with(PageResponse::setMsg, res.getMsg())
            .with(PageResponse::setResponseBody, res.getResponseBody())
            .with(PageResponse::setSize, size)
            .with(PageResponse::setNumber, pageInfo.getPageNum())
            .with(PageResponse::setTotalPages, totalPages)
            .with(PageResponse::setNumberOfElements, list.size())
            .build();
      }
    }
    return res;
  }

  private static ErrorResponse handlerFailed(Res res) {
    return Builder.of(ErrorResponse::new)
        .with(ErrorResponse::setStatus, EnhanceConstants.FAILED)
        .with(ErrorResponse::setCode, 205)
        .with(ErrorResponse::setMsg, res.getMsg())
        .with(ErrorResponse::setResponseBody, res.getResponseBody())
        .build();
  }

  private static PartialSuccesResponse handlerPartialSuccess(Res res) {
    return Builder.of(PartialSuccesResponse::new)
        .with(PartialSuccesResponse::setStatus, EnhanceConstants.PARTIAL_SUCCESS)
        .with(PartialSuccesResponse::setCode, 201)
        .with(PartialSuccesResponse::setMsg, res.getMsg())
        .with(PartialSuccesResponse::setResponseBody, res.getResponseBody())
        .with(PartialSuccesResponse::setFailureData, res.getPartialFailed())
        .build();
  }

  @Override
  public Result handler(Res baseRes) {
    Map<String, Function<Res, Result>> map = new HashMap<>();
    map.put("FAILED", CommonRes::handlerFailed);
    map.put("SUCCESS", CommonRes::handlerSuccess);
    map.put("PARTIAL_SUCCESS", CommonRes::handlerPartialSuccess);
    return map.get(this.operation).apply(baseRes);
  }
}
