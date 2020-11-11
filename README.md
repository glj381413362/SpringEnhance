# 异常帮助类

![image-20200720233646282](https://user-images.githubusercontent.com/19701761/98550907-a36bdc00-22d7-11eb-9675-d8b876bc1d74.png)

## 普通抛出异常

```java
    if (1==1){
			throw new BaseException("我错了");
		}
		// 支持参数替换
		if (1==1){
			throw new BaseException(Msg.of("学生:{}错了"),"student001");
		}
		try{
		  // 业务
    }catch (Exception e){
      throw new BaseException(e,"学生:{}错了","student001");
    }
```

## 异常帮助类抛出异常

1. 不使用异常帮助类，需要判断是否为空，然后返回或手动抛出异常：

   ```java
     Order order = orderMapper.getOne(orderId);
       if (order == null) {
         throw new BaseException(Msg.of("根据orderId[{}]未查询到订单"),orderId);
       }
   // 或者
       User user = userMapper.getOne(userCode);
       if (user == null) {
         throw new BaseException(Msg.of("根据userCode[{}]未查询到用户"),userCode);
       }
   ```

2. 使用异常帮助类，不需要判断是否为空：

   ```java
   // 静态导入方式使用
   import static com.enhance.logplugin.demo.exception.OrderExceptionAssert.ORDER_NOT_FOUND;
   
   Order order = orderMapper.getOne(orderId);
   ORDER_NOT_FOUND.assertNotNull(order,"根据orderId[{}]未查询到订单",orderId);
   
   
   User user = userMapper.getOne(userCode);    UserExceptionAssert.USER_NOT_FOUND.assertNotNull(user,"根据userCode[{}]未查询到用户",userCode);
   
   
   ```

3. 使用异常帮助类抛出异常

   ```java
      Order order2 = orderMapper.getOne(orderId);
      if (order2 == null) {
         OrderExceptionAssert.ORDER_NOT_FOUND.throwE();
       }
   
       Order order3 = null;
       try {
         order3 = orderMapper.getOne(orderId);
       } catch (Exception e) {
         OrderExceptionAssert.ORDER_NOT_FOUND.throwE(e,"根据orderId[{}]查询订单异常",orderId);
       }
   ```

# 统一接口返回结构

## 为什么要统一返回结构

在移动互联网，分布式、微服务盛行的今天，现在项目绝大部分都采用的微服务框架，前后端分离方式，如果不规定一个统一的结构，沟通起来就会浪费很多时间。

## 需要解决的场景

1. 二次开发，有的接口已经有自己的响应结构体，但是与自己的结构体又不相同。
2. 某些对外接口结构体已经固定，不同于统一的结构体。

## 如何实现

一开始想到的是使用AOP，利用切面对@RequestMapping注解的方法统一处理，但是发现这种方式虽然实现简单，但是不能够改变返回结果，无法解决场景一的问题，除非方法的返回值统一为Object类型，所以果断舍弃掉。最后选择使用@ControllerAdvice+ResponseBodyAdvice完美解决统一接口返回结果。

## 如何使用

### 依赖安装

目前maven中央仓库还未推送，所以只能下载源码，然后`mvn install`到自己的maven仓库，最后在项目中添加如下依赖:

```xml
 <dependency>
      <groupId>com.enhance</groupId>
      <artifactId>spring-enhance</artifactId>
      <version>0.0.1-SNAPSHOT</version>
 </dependency>
```

### 实现效果



## 具体实现

这里定义了具体的几类（成功、部分成功、失败、分页、异常）返回结果对象。

### Result

统一返回结构体的基础类，返回结果包含status、code、message、responseBody和CommonRes，code和message可通过在配置文件responseCode.properties或者多语言文件responseCode_en_US.properties和responseCode_zh_CN.properties进行配置，两个值是不需要设置的，会通过返回的status在配置文件转化出来。CommonRes不会返回给前台，是用于构建统一返回结果的一个帮助字段，可以配合Res使用，具体使用后面介绍。

```java 
@Slf4j
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result<T> implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonIgnore 
  private CommonRes res = CommonRes.SUCCESS;

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
```

### PartialSuccesResponse

部分成功返回结构体，当我们批量处理一批数据时的返回结构体，继承Result，多一个一个字段failureData，用于返回失败的数据，或失败数据的原因。

```java
@Data
public class PartialSuccesResponse<T> extends Result {
  @ApiModelProperty(value = "部分失败的数据,部分失败时才会有数据")
  private T failureData;
  public PartialSuccesResponse() {
    setStatus(EnhanceConstants.PARTIAL_SUCCESS);
  }

}
```

### PageResponse

分页查询结果的结构对象，继承Result，多了一下与分页相关的字段。

```java
@Data
public class PageResponse extends Result {
  private Integer totalPages;
  private Long totalElements;
  private Integer numberOfElements;
  private Integer size;
  private Integer number;
  public PageResponse() {
        setStatus(EnhanceConstants.SUCCESS);
  }
}
```

### ExceptionResponse

方法处理异常时的统一返回结果对象，继承Result，多了一些异常信息相关的字段，测试开发阶段可直接把异常堆栈返回给前端，方便查找错误。

```java
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
```

### ErrorResponse

方法处理失败时的响应结果对象，继承Result，只是用于区分成功和失败，目前没有其他多余的字段。

```java
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
```

这里可能有人会问，前端如何判断请求处理是否成功，是通过status字段还是code呢？其实都不是，由于isFailed()和isSuccess()方法的存在，json序列化时会添加success、failed两个字段，前端可通过这两个字段进行判断。

### ResponseCodeProperty

很多公司对于返回的code和错误信息都有自己的规定，每个code对应特定的错误，方便错误定位。所以这里我并没有将code和message写死到程序，而是放到了一个配置文件中，方便阅读，方便追溯错误信息，也方便相关信息的修改。

```java
@Data
@Slf4j
@AllArgsConstructor
public class ResponseCodeProperty {
  private Properties responseCodeProperties;
  private GetLanguageService getLanguageService;
  private SpringResponseProperty springResponseProperty;
  private ResponseCodeProperty() {
    BusinessExceptionAssert.ILLEGAL_OPERATION_EXCEPTION.throwE();
  }
  public ResponseCodeProperty(GetLanguageService getLanguageService) {
    this.getLanguageService = getLanguageService;
  }
  public int getCode(String status, int defaultValue) {
    Assert.notNull(status, "status 为空");
    try {
      String code = status.toLowerCase();
      if (springResponseProperty.isMultilingual()) {
        Locale language = getLanguageService.getLanguage();
        if (language == null) {
          language = LocaleUtils.toLocale("zh_CN");
        }
        Message message = MessageHelper.getMessage(code, null, language);
        if (message == null) {
          log.warn("未维护response的status[{}]对应的code,使用默认值[{}]", status, defaultValue);
          return defaultValue;
        }
        return Integer.parseInt(message.getDesc());
      } else {
        String property =
            responseCodeProperties.getProperty(code.toLowerCase(), String.valueOf(defaultValue));
        return Integer.parseInt(property);
      }
    } catch (Exception e) {
      log.warn("ResponseCodeProperty getCode error {} ", e);
    }
    return defaultValue;
  }
  public String getMsg(String status, String defaultValue) {
    Assert.notNull(status, "status 为空");
    try {
      String code = status.toLowerCase();
      if (springResponseProperty.isMultilingual()) {
        Locale language = getLanguageService.getLanguage();
        if (language == null) {
          language = LocaleUtils.toLocale("zh_CN");
        }
        Message message = MessageHelper.getMessage(code + ".msg", null, language);
        if (message == null) {
          log.warn("未维护response的status[{}]对应的msg,使用默认值[{}]", status, defaultValue);
          return defaultValue;
        }
        return message.getDesc();
      } else {
        String property = responseCodeProperties.getProperty(code + ".msg", defaultValue);
        return property;
      }
    } catch (Exception e) {
      log.warn("ResponseCodeProperty getMsg error {} ", e);
    }
    return defaultValue;
  }
}
```

### GetLanguageService

当配置了使用多语言结果返回时，必须实现接口GetLanguageService，用于获取该次请求使用的语言类型。

```java
public interface GetLanguageService {
  Locale getLanguage();
}
```

### ResponseConvertService

主要用于解决场景2，用户可将返回结果重新组装成统一结构进行返回。

```java
public interface ResponseConvertService<S, T> {
  int order();
  boolean supports(Object result);
  T convert(ResponseCodeProperty responseCodeProperty,S result);
}
```

### DefaultResponseConvertService

项目提供的默认ResponseConvertService实现，用于处理接口返回未包装的接口数据。

```java
public class DefaultResponseConvertService implements ResponseConvertService<Object, Result> {
  @Override
  public int order() {
    return Integer.MAX_VALUE;
  }
  @Override
  public boolean supports(Object result) {
    return true;
  }
  @Override
  public Result convert(ResponseCodeProperty responseCodeProperty, Object source) {
    String status = EnhanceConstants.SUCCESS;
    int code = responseCodeProperty.getCode(status, 200);
    String msg = responseCodeProperty.getMsg(status, EnhanceConstants.SUCCESS);
    return Builder.of(Res::new)
        .with(Res::setStatus, status)
        .with(Res::setCode, code)
        .with(Res::setMsg, msg)
        .with(Res::setResponseBody, source)
        .build();
  }
}
```

### ResponseBodyHandler

对接口返回的数据进行处理，可通过配置选择某些处理某些不处理。

```java
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
```

以上就是统一接口返回结果的核心类。

## 工具类Res

Res是用于统一返回结果的一个工具类，使用Res可以方便方法与方法间的结果传递。可以更方便快捷的构建统一的接口结果。

### CommonRes

用于构建成功、部分成功、失败的响应结果。

```java
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
```

### Res

Res继承Result，多了pageInfo、partialSuccess、partialFailed、hasBeenBuild四个属性，pageInfo用于存放分页查询相关信息，partialSuccess和partialFailed用于批量操作时部分成功的数据，hasBeenBuild用来表示该对象是否调用了buildResponse()方法。

```java
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
  public static Res success() {
    return Builder.of(Res::new).with(Res::setRes, CommonRes.SUCCESS).build();
  }
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
  public static Res failed() {
    return Builder.of(Res::new).with(Res::setRes, CommonRes.FAILED).build();
  }
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

```

