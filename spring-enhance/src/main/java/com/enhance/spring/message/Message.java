package com.enhance.spring.message;

/**
 * 消息及类型
 *
 */
public class Message {

  private String code;

  private String desc;

  public Message() {}

  /** 构建消息，默认消息类型为 警告(WARN) 基本 */
  public Message(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public String code() {
    return code;
  }

  public String desc() {
    return desc;
  }

  public String getCode() {
    return code;
  }

  public Message setCode(String code) {
    this.code = code;
    return this;
  }

  public String getDesc() {
    return desc;
  }

  public Message setDesc(String desc) {
    this.desc = desc;
    return this;
  }
}
