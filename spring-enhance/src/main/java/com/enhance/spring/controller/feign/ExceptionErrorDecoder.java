package com.enhance.spring.controller.feign;

import com.common.tools.util.FastJsonUtil;
import com.common.tools.util.exception.BaseException;
import com.common.tools.util.exception.FeignClientException;
import com.enhance.spring.controller.data.ExceptionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("springEnhanceExceptionErrorDecoder")
@Slf4j
public class ExceptionErrorDecoder implements ErrorDecoder {
  ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Exception decode(String s, Response response) {

    FeignClientException feignClientException = new FeignClientException(s, "访问远程服务异常");

    try {
      if (response.body() != null) {
        String body = Util.toString(response.body().asReader());
        feignClientException.setBusinessUrl(response.request().url());
        ExceptionResponse ei =
            this.objectMapper.readValue(body.getBytes("UTF-8"), ExceptionResponse.class);
        String exception = ei.getException();
        Class clazz = Class.forName(exception);
        Object obj = clazz.newInstance();
        String message = ei.getMessage();
        String targetMsg = null;
        if (obj instanceof BaseException) {
          targetMsg = message.substring(message.indexOf("{"), message.indexOf("}") + 1);
          BaseException businessException = FastJsonUtil.toBean(targetMsg, BaseException.class);
          return businessException;
        }
        feignClientException.setRemoteCode(ei.getCode());
        feignClientException.setRemoteException(ei.getMessage());
        feignClientException.setRemoteMessage(ei.getUserMsg());
        return feignClientException;
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return feignClientException;
  }
}
