package com.enhance.spring.controller.exception;

import com.common.tools.util.exception.AssertException;
import com.common.tools.util.exception.BaseException;
import com.common.tools.util.exception.Status;
import com.enhance.spring.config.properties.SpringResponseProperty;
import com.enhance.spring.controller.data.ErrorResponse;
import com.enhance.spring.controller.data.ExceptionResponse;
import com.enhance.spring.controller.data.Res;
import com.enhance.spring.controller.data.Result;
import com.google.common.collect.Lists;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@ControllerAdvice
@ConditionalOnWebApplication
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UnifiedExceptionHandler {

  private static final String DEFAULT_VALUE = "doesn't have a default value";
  private final SpringResponseProperty springResponseProperty;
  /** 当前环境 */
  @Value("${spring.profiles.active: default}")
  private String profile;

  /** 处理400类异常 */

  /**
   * 违反约束异常处理
   *
   * @param e
   * @author gongliangjun 2020-06-15 10:49 PM
   * @return com.enhance.spring.controller.data.Res
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ConstraintViolationException.class)
  public Result handleConstraintViolationException(
      ConstraintViolationException e, HttpServletRequest request) {
    log.error(
        "handleConstraintViolationException error, uri:{}, caused by: ",
        request.getRequestURI(),
        e);
    Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
    List<ParameterInvalid> parameterInvalids = convertToParameterInvalids(constraintViolations);
    ExceptionResponse res =
        new ExceptionResponse(e, Status.of("constraint.violation.exception.error")) {
          {
            setResponseBody(parameterInvalids);
          }
        };
    this.setDevException(res, e);
    return res;
  }

  /**
   * json反序列化失败的异常处理 违反约束异常
   *
   * @param e
   * @param request
   * @author gongliangjun 2020-06-15 10:49 PM
   * @return com.enhance.spring.controller.data.Res
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public Result handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e, HttpServletRequest request) {
    log.error(
        "handleHttpMessageNotReadableException error, uri:{}, caused by: ",
        request.getRequestURI(),
        e);
    ExceptionResponse res =
        new ExceptionResponse(e, Status.of("http.message.not.readable.exception.error"));
    this.setDevException(res, e);
    return res;
  }

  /**
   * 处理参数绑定时异常处理
   *
   * @param e
   * @param request
   * @author gongliangjun 2020-06-15 10:49 PM
   * @return com.enhance.spring.controller.data.Res
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({BindException.class})
  public Result handleBindException(BindException e, HttpServletRequest request) {
    log.error("handleBindException error, uri:{}, caused by: ", request.getRequestURI(), e);
    BindingResult bindingResult = e.getBindingResult();
    List<ParameterInvalid> parameterInvalids = convertBindingResult(bindingResult);
    ExceptionResponse res =
        new ExceptionResponse(e, Status.of("bind.exception.error")) {
          {
            setResponseBody(parameterInvalids);
          }
        };
    this.setDevException(res, e);
    return res;
  }
  /**
   * 使用@Validated注解时，参数验证错误异常处理
   *
   * @param e
   * @param request
   * @author gongliangjun 2020-06-15 10:49 PM
   * @return com.enhance.spring.controller.data.Res
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({MethodArgumentNotValidException.class})
  public Result handleValidException(BindException e, HttpServletRequest request) {
    log.error("handleValidException error, uri:{}, caused by: ", request.getRequestURI(), e);
    BindingResult bindingResult = e.getBindingResult();
    List<ParameterInvalid> parameterInvalids = convertBindingResult(bindingResult);
    ExceptionResponse res =
        new ExceptionResponse(e, Status.of("method.argument.not.valid.exception.error")) {
          {
            setResponseBody(parameterInvalids);
          }
        };
    this.setDevException(res, e);
    return res;
  }

  /**
   * 处理未授权异常处理
   *
   * @param e
   * @param request
   * @author gongliangjun 2020-06-15 10:49 PM
   * @return com.enhance.spring.controller.data.Res
   */
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(AccessDeniedException.class)
  public Result handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
    log.error("handleAccessDeniedException error, uri:{}, caused by: ", request.getRequestURI(), e);
    ExceptionResponse res =
        new ExceptionResponse(e, Status.of("unauthorized.error")) {
          {
            setCode(401);
            setUserMsg("未授权");
          }
        };
    this.setDevException(res, e);
    return res;
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(IllegalArgumentException.class)
  public Result handleIllegalArgumentException(
      IllegalArgumentException e, HttpServletRequest request) {
    log.error(
        "handleIllegalArgumentException error, uri:{}, caused by: ", request.getRequestURI(), e);
    ExceptionResponse res =
        new ExceptionResponse(e, Status.of("illegal.argument.error")) {
          {
            setCode(500);
            setUserMsg("非法参数");
          }
        };
    this.setDevException(res, e);
    return res;
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(SQLException.class)
  public Result handleSQLException(SQLException e, HttpServletRequest request) {
    log.error("handleSQLException  uri:{}, caused by: ", request.getRequestURI(), e);
    String str = e.getMessage();
    Result res = new ErrorResponse(e);
    res.setStatus("sql.error");
    if (str.contains(DEFAULT_VALUE)) {
      // 获得第一个点的位置
      int indexStart = str.indexOf("'");
      // 根据第一个点的位置 获得第二个点的位置
      int indexEnd = str.indexOf("'", indexStart + 1);
      // 根据第二个点的位置，截取 字符串。得到结果 result
      String result = str.substring(indexStart + 1, indexEnd);
      String message = String.format("字段[%s]缺少必输值", result);
      res =
          new ExceptionResponse(e, Status.of("sql.missing.default.value.error")) {
            {
              setUserMsg(message);
            }
          };
    }
    this.setDevException(res, e);
    return res;
  }

  /**
   * base异常
   *
   * @param e 异常
   * @return 异常结果
   */
  @ExceptionHandler(value = BaseException.class)
  public Result handleBusinessException(BaseException e) {
    Result res = new ExceptionResponse(e);
    this.setDevException(res, e);
    return res;
  }
  /**
   * Assert异常
   *
   * @param e 异常
   * @return 异常结果
   */
  @ExceptionHandler(value = AssertException.class)
  public Result handleBusinessException(AssertException e) {
    Result res = new ExceptionResponse(e);
    this.setDevException(res, e);
    return res;
  }

  /**
   * 未定义异常
   *
   * @param e 异常
   * @return 异常结果
   */
  @ExceptionHandler(value = Exception.class)
  public ExceptionResponse handleException(Exception e) {
    ExceptionResponse res = new ExceptionResponse(e);
    this.setDevException(res, e);
    return res;
  }

  private void setDevException(Result er, Exception ex) {
    List<String> profiles = springResponseProperty.getPrintStackProfiles();
    if (er instanceof ExceptionResponse) {
      ((ExceptionResponse) er).setOriginalException(ex.getMessage());
      if (profiles.contains(this.profile)) {
        ((ExceptionResponse) er).setTrace(ex.getStackTrace());
        Throwable cause = ex.getCause();
        if (cause != null) {
          ((ExceptionResponse) er).setThrowable(cause.getMessage(), cause.getStackTrace());
        }
      }
    }
  }

  private List<ParameterInvalid> convertBindingResult(BindingResult bindingResult) {
    if (bindingResult == null) {
      return null;
    }
    List<ParameterInvalid> parameterInvalidItemList = Lists.newArrayList();
    List<FieldError> fieldErrorList = bindingResult.getFieldErrors();
    for (FieldError fieldError : fieldErrorList) {
      ParameterInvalid parameterInvalidItem = new ParameterInvalid();
      parameterInvalidItem.setFieldName(fieldError.getField());
      parameterInvalidItem.setMessage(fieldError.getDefaultMessage());
      parameterInvalidItemList.add(parameterInvalidItem);
    }

    return parameterInvalidItemList;
  }

  private List<ParameterInvalid> convertToParameterInvalids(Set<ConstraintViolation<?>> cvset) {
    if (CollectionUtils.isEmpty(cvset)) {
      return null;
    }
    List<ParameterInvalid> parameterInvalidItemList = Lists.newArrayList();
    for (ConstraintViolation<?> cv : cvset) {
      ParameterInvalid parameterInvalidItem = new ParameterInvalid();
      String propertyPath = cv.getPropertyPath().toString();
      if (propertyPath.indexOf(".") != -1) {
        String[] propertyPathArr = propertyPath.split("\\.");
        parameterInvalidItem.setFieldName(propertyPathArr[propertyPathArr.length - 1]);
      } else {
        parameterInvalidItem.setFieldName(propertyPath);
      }
      parameterInvalidItem.setMessage(cv.getMessage());
      parameterInvalidItemList.add(parameterInvalidItem);
    }

    return parameterInvalidItemList;
  }

  class ParameterInvalid {

    /** 无效字段的名称 */
    private String fieldName;

    /** 错误信息 */
    private String message;

    public String getFieldName() {
      return fieldName;
    }

    public void setFieldName(String fieldName) {
      this.fieldName = fieldName;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    @Override
    public String toString() {
      return "ParameterInvalidDTO{"
          + "fieldName='"
          + fieldName
          + '\''
          + ", message='"
          + message
          + '\''
          + '}';
    }
  }
}
