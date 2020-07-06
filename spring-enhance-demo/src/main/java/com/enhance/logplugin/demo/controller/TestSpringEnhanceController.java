package com.enhance.logplugin.demo.controller;

import com.common.tools.util.exception.BaseException;
import com.common.tools.util.exception.BusinessExceptionAssert;
import com.common.tools.util.exception.Msg;
import com.enhance.logplugin.demo.controller.dto.OtherResult;
import com.enhance.logplugin.demo.controller.dto.TestApplicationHelper;
import com.enhance.logplugin.demo.controller.dto.TestApplicationHelperAsyncStatic;
import com.enhance.logplugin.demo.dao.OrderMapper;
import com.enhance.logplugin.demo.entity.Order;
import com.enhance.logplugin.demo.filter.UserInfoService;
import com.enhance.logplugin.demo.service.OrderService;
import com.enhance.spring.config.properties.ResponseCodeProperty;
import com.enhance.spring.controller.data.Res;
import com.enhance.spring.controller.data.Result;
import com.enhance.spring.helper.ApplicationContextHelper;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** @author gongliangjun 2019/07/01 11:18 */
@RestController
@RequestMapping(value = "/test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestSpringEnhanceController {
  static {
  }

  private final OrderMapper orderMapper;
  private final OrderService orderService;
  private final ResponseCodeProperty responseCodeProperty;
  private TestApplicationHelper testApplicationHelper;

  @GetMapping("/testApplicationHelper")
  public Res testHelper() {
    testApplicationHelper = new TestApplicationHelper();
    ApplicationContextHelper.asyncInstanceSetter(
        UserInfoService.class, testApplicationHelper, "setUserInfoService");

    boolean aNull = testApplicationHelper.isNull();
    return Res.successBody(aNull);
  }

  @GetMapping("/testApplicationHelperAsyncStatic")
  public Res testHelperAsyncStatic() {
    boolean aNull = TestApplicationHelperAsyncStatic.isNull();
    return Res.successBody(aNull);
  }

  @GetMapping("/testOtherResult")
  public OtherResult testOtherResult() {
    Properties responseCodeProperties = responseCodeProperty.getResponseCodeProperties();
    OtherResult otherResult = new OtherResult();
    otherResult.setCode("false");
    otherResult.setMsg("test");
    otherResult.setData(responseCodeProperties);
    return otherResult;
  }
  @GetMapping("/testResponseCode")
  public Result test() {
    Properties responseCodeProperties = responseCodeProperty.getResponseCodeProperties();
    return Res.successBody(responseCodeProperties).buildResponse();
  }

  @GetMapping("/testOne")
  public Order testOne() {
    return orderMapper.findAll().get(0);
  }

  @GetMapping("/testPage")
  public List<Order> testPage() {
    return orderMapper.findAll();
  }

  @GetMapping("/testRes")
  public Result testRes() {
    Res res = Res.successBody(orderService.listOrder(new Order()));
    return res.buildResponse();
  }

  @GetMapping("/testResPartialSucces")
  public Result testResPartialSucces() {
    Res<Object> build = Res.newInstance();
    build.add2Failed(
        new Order() {
          {
            setDesc("失败了");
          }
        });
    build.add2Success(
        new Order() {
          {
            setDesc("成功了");
          }
        });
    Result res = build.buildResponse();
    return res;
  }

  @GetMapping("/testBaseException")
  public Result testBaseException() {
    Res<Object> build = Res.newInstance();
    build.add2Failed(
        new Order() {
          {
            setDesc("失败了");
          }
        });
    build.add2Success(
        new Order() {
          {
            setDesc("成功了");
          }
        });
    if (1 == 1) {
      throw new BaseException(Msg.of("测试{}"), "抛异常");
    }
    return build.buildResponse();
  }

  @GetMapping("/testExceptionAssert")
  public Result testExceptionAssert() {
    Res<Object> build = Res.newInstance();
    build.add2Failed(
        new Order() {
          {
            setDesc("失败了");
          }
        });
    build.add2Success(
        new Order() {
          {
            setDesc("成功了");
          }
        });
    Order order = null;
    BusinessExceptionAssert.ILLEGAL_OPERATION_EXCEPTION.assertNotNull(order, "测试");
    return build.buildResponse();
  }
}
