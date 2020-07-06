package com.enhance.spring.config.properties;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 日志插件配置类
 *
 * @author gongliangjun 2020/06/16 2:12 PM
 */
@Data
public class SpringResponseProperty {
  public static final String SPING_ENHANCE_PREFIX = "plugin.spring.response";
  /**
   * 是否开启统一响应结果 对语言支持
   * 默认开启
   */
  private boolean multilingual;
  /**
   * 支持统一响应结果的controller类前缀
   * 一般是项目的类路径到controller层，支持多个
   */
  private List<String> supportClassPrefix;

  private List<String> printStackProfiles;

  public SpringResponseProperty() {
    this.multilingual = true;
    this.supportClassPrefix =
        new ArrayList<String>() {
          {
            add("com.glj.demo");
          }
        };
    this.printStackProfiles =
        new ArrayList<String>() {
          {
            add("test");
            add("dev");
            add("sit");
            add("uat");
          }
        };
  }
}
