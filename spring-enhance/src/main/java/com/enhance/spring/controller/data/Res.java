package com.enhance.spring.controller.data;

import static com.common.tools.util.exception.BusinessExceptionAssert.RES_BUILD_EXCEPTION;

import com.common.tools.util.Builder;
import com.common.tools.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.pagehelper.PageInfo;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

/**
 * description
 *
 * @author 龚梁钧 2019/06/13 18:07
 */
/**
 * <p>
 * 统一返回结构service过渡DTO
 * 一般用于service作为返回结果，controller层无需判断是否成功，只需调用buildResponse方法即可
 * 如果开启全局统一返回结果，不调用buildResponse也是可以，ResponseBodyHandler内会进行判断，然后做相应的处理
 * </p>
 *
 * @author gongliangjun 2020/06/20 5:28 PM
 */
@Slf4j
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Res<T> extends Result<T> {

  /** 分页查询 分页信息 */
  @JsonIgnore private PageInfo pageInfo;
  /** 批量操作时，可能部分成功 */
  @JsonIgnore private List<Object> partialSuccess;
  /** 批量操作时，可能部分失败 */
  @JsonIgnore private List<Object> partialFailed;

  @JsonIgnore
  private transient boolean hasBeenBuild;

  public static Res newInstance() {
    return Builder.of(Res::new).with(Res::setRes, CommonRes.SUCCESS).build();
  }
  /**
   * 构建成功返回
   *
   *
   * @author gongliangjun 2020-07-05 5:01 PM
   * @return com.enhance.spring.controller.data.Res
   */
  public static Res success() {
    return Builder.of(Res::new).with(Res::setRes, CommonRes.SUCCESS).build();
  }

  /**
   * 构建成功返回
   *
   * @param msg
   * @return com.enhance.spring.controller.data.Res
   * @author 龚梁钧 2019-06-14 15:21
   */
  public static Res success(String msg) {
    return Builder.of(Res::new).with(Res::setRes, CommonRes.SUCCESS).with(Res::setMsg, msg).build();
  }

  public static <T> Res successBody(T responseBody) {
    return Builder.of(Res::new)
        .with(Res::setRes, CommonRes.SUCCESS)
        .with(Res::setResponseBody, responseBody)
        .build();
  }

  public static <T> Res successBody(T responseBody, String msg) {
    return Builder.of(Res::new)
        .with(Res::setRes, CommonRes.SUCCESS)
        .with(Res::setResponseBody, responseBody)
        .with(Res::setMsg, msg)
        .build();
  }

  public static <T> Res successBody(T responseBody, PageInfo pageInfo) {
    return Builder.of(Res::new)
        .with(Res::setRes, CommonRes.SUCCESS)
        .with(Res::setResponseBody, responseBody)
        .with(Res::setPageInfo, pageInfo)
        .build();
  }

  /**
   * 构建失败返回 默认失败返回信息
   *
   * @return com.enhance.spring.controller.data.Res
   * @author 龚梁钧 2019-06-14 15:21
   */
  public static Res failed() {
    return Builder.of(Res::new).with(Res::setRes, CommonRes.FAILED).build();
  }

  /**
   * 构建失败返回 自定义失败返回信息
   *
   * @param msg
   * @return com.enhance.spring.controller.data.Res
   * @author 龚梁钧 2019-06-14 15:21
   */
  public static Res failed(String msg) {
    return Builder.of(Res::new).with(Res::setRes, CommonRes.FAILED).with(Res::setMsg, msg).build();
  }

  public static Res failedMsg(String msgTemplate, String... msg) {
    return Builder.of(Res::new)
        .with(Res::setRes, CommonRes.FAILED)
        .with(Res::setMsg, StringUtil.strFormat(msgTemplate, msg))
        .build();
  }

  public static <T> Res failed(T responseBody, String msg) {
    return Builder.of(Res::new)
        .with(Res::setRes, CommonRes.FAILED)
        .with(Res::setMsg, msg)
        .with(Res::setResponseBody, responseBody)
        .build();
  }

  public static <T> Res failedBody(T responseBody) {
    return Builder.of(Res::new)
        .with(Res::setRes, CommonRes.FAILED)
        .with(Res::setResponseBody, responseBody)
        .build();
  }

  /**
   * 构建请求响应
   *
   *
   * @author gongliangjun 2020-07-05 5:01 PM
   * @return com.enhance.spring.controller.data.Result
   */
  public Result buildResponse() {

    // ===============================================================================
    //  partialFailed和partialSuccess都不为空，则部分成功
    // ===============================================================================
    if (CollectionUtils.isNotEmpty(this.partialFailed)
        && CollectionUtils.isNotEmpty(this.partialSuccess)) {
      this.setRes(CommonRes.PARTIAL_SUCCESS);
      this.setResponseBody((T) this.partialSuccess);
    } else if (CollectionUtils.isNotEmpty(this.partialFailed)) {
      this.setRes(CommonRes.FAILED);
      this.setResponseBody((T) this.partialFailed);
    } else if (CollectionUtils.isNotEmpty(this.partialSuccess)) {
      this.setRes(CommonRes.SUCCESS);
      this.setResponseBody((T) this.partialSuccess);
    } else {
      RES_BUILD_EXCEPTION.assertNotNull(this.getRes(), "CommonRes不能为空");
    }
    this.hasBeenBuild = true;
    return this.getRes().handler(this);
  }

  public void add2Failed(String failedCode, String failedMsg) {
    String strFormat = StringUtil.strFormat("{},{}", failedCode, failedMsg);
    if (CollectionUtils.isNotEmpty(this.partialFailed)) {
      this.partialFailed.add(strFormat);
    } else {
      List<Object> partialFailed = new ArrayList<>();
      partialFailed.add(strFormat);
      this.partialFailed = partialFailed;
    }
  }

  public void add2Failed(Object object) {
    if (CollectionUtils.isNotEmpty(this.partialFailed)) {
      this.partialFailed.add(object);
    } else {
      List<Object> partialFailed = new ArrayList<>();
      partialFailed.add(object);
      this.partialFailed = partialFailed;
    }
  }

  public void add2Success(Object success) {
    if (CollectionUtils.isNotEmpty(this.partialSuccess)) {
      this.partialSuccess.add(success);
    } else {
      List<Object> partialSuccess = new ArrayList<>();
      partialSuccess.add(success);
      this.partialSuccess = partialSuccess;
    }
  }

  public PageInfo getPageInfo() {
    return pageInfo;
  }

  protected void setPageInfo(PageInfo pageInfo) {
    this.pageInfo = pageInfo;
  }

  public List<Object> getPartialSuccess() {
    return partialSuccess;
  }

  protected void setPartialSuccess(List<Object> partialSuccess) {
    this.partialSuccess = partialSuccess;
  }

  public List<Object> getPartialFailed() {
    return partialFailed;
  }

  protected void setPartialFailed(List<Object> partialFailed) {
    this.partialFailed = partialFailed;
  }

  public boolean isHasBeenBuild() {
    return hasBeenBuild;
  }
}
