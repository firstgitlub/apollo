package com.ctrip.framework.apollo.spring.property;

public class SpringValueDefinition {

  private final String key;
  private final String placeholder;
  private final String propertyName;

  public SpringValueDefinition(String key, String placeholder, String propertyName) {
    // 参数key
    this.key = key;
    // 参数占位符
    this.placeholder = placeholder;
    // bean 属性名称
    this.propertyName = propertyName;
  }

  public String getKey() {
    return key;
  }

  public String getPlaceholder() {
    return placeholder;
  }

  public String getPropertyName() {
    return propertyName;
  }
}
