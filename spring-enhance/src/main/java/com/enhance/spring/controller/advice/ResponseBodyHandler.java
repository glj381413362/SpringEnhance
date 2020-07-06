package com.enhance.spring.controller.advice;

import com.common.tools.util.Builder;
import com.enhance.spring.config.properties.ResponseCodeProperty;
import com.enhance.spring.config.properties.SpringResponseProperty;
import com.enhance.spring.controller.ResponseConvertService;
import com.enhance.spring.controller.data.CommonRes;
import com.enhance.spring.controller.data.ExceptionResponse;
import com.enhance.spring.controller.data.PageResponse;
import com.enhance.spring.controller.data.Res;
import com.enhance.spring.controller.data.Result;
import com.github.pagehelper.Page;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 返回Body处理 body使用的消息转换器必须是AbstractJackson2HttpMessageConverter才会生效。
 *
 * @author gongliangjun 2020/06/15 12:06 PM
 */
@ControllerAdvice
@Slf4j
public class ResponseBodyHandler implements ResponseBodyAdvice<Object> {
  private static final String FLAG = "useWrap";

  @Autowired private SpringResponseProperty springResponseProperty;
  @Autowired private ResponseCodeProperty responseCodeProperty;
  @Autowired private List<ResponseConvertService> responseConvertServices;

  // 第一个调用的。判断当前的拦截器（advice是否支持）
  // 注意它的入参有：方法参数、所使用的消息转换器
  @Override
  public boolean supports(
      MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    Method method = returnType.getMethod();
    // ===============================================================================
    //  判断是否是全局异常处理类
    // ===============================================================================
    ExceptionHandler methodAnnotation = returnType.getMethodAnnotation(ExceptionHandler.class);
    if (methodAnnotation != null) {
      return true;
    }
    String name = method.getDeclaringClass().getName();

    List<String> supportClassPrefix = springResponseProperty.getSupportClassPrefix();
    Optional<String> first = supportClassPrefix.stream().filter(s -> name.startsWith(s)).findAny();
    if (first.isPresent()) {
      return true;
    }
    return false;
  }

  @Override
  public Object beforeBodyWrite(
      Object object,
      MethodParameter returnType,
      MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {
    if (!useWrap(request)) {
      return object;
    }
    Result result = null;
    try {

      FLAG:
      if (object instanceof Result) {
        if (object instanceof Res) {
          if (((Res) object).isHasBeenBuild()) {
            result = (Res) object;
          } else {
            result = ((Res) object).buildResponse();
          }
        } else {
          result = (Result) object;
        }
        Integer resultCode = result.getCode() == null ? 200 : result.getCode();
        String status = result.getStatus();

        int code = responseCodeProperty.getCode(status, resultCode);
        String msg;
        if (object instanceof ExceptionResponse
            && StringUtils.isNotBlank(((ExceptionResponse) object).getUserMsg())) {
          msg = ((ExceptionResponse) object).getUserMsg();
        } else {
          msg = responseCodeProperty.getMsg(status, result.getMessage());
        }
        result.setCode(Integer.valueOf(code));
        result.setMsg(msg);
      } else if (object instanceof Page) {
        Page page = (Page) object;
        long total = page.getTotal();
        int size = page.size();
        int totalPages = (int) (total - 1) / size + 1;
        String status = CommonRes.SUCCESS.name();
        int code = responseCodeProperty.getCode(status, 200);
        String msg = responseCodeProperty.getMsg(status, result.getMsg());
        result =
            Builder.of(PageResponse::new)
                .with(PageResponse::setStatus, status)
                .with(PageResponse::setCode, code)
                .with(PageResponse::setMsg, msg)
                .with(PageResponse::setSize, size)
                .with(PageResponse::setNumber, page.getPageNum())
                .with(PageResponse::setTotalPages, totalPages)
                .with(PageResponse::setNumberOfElements, page.size())
                .build();
      } else {
        List<ResponseConvertService> services =
            responseConvertServices.stream()
                .filter(responseConvertService -> responseConvertService.supports(object))
                .sorted(Comparator.comparing(ResponseConvertService::order))
                .collect(Collectors.toList());
        ResponseConvertService responseConvertService = services.get(0);
        return responseConvertService.convert(responseCodeProperty, object);
      }
    } catch (Exception e) {
      log.warn("统一响应处理异常:{}", e);
      return object;
    }
    return result;
  }

  public Boolean useWrap(ServerHttpRequest request) {
    // 包含 useWrap 且值不为0、N和false的时候，返回true
    if (request.getHeaders().containsKey(FLAG)) {
      List<String> flagList = request.getHeaders().get(FLAG);
      for (String flag : flagList) {
        if (StringUtils.equals("0", flag)
            || StringUtils.equals("N", flag)
            || StringUtils.equals("false", flag)) {
          return false;
        }
      }
      return true;
    }
    return true;
  }
}
