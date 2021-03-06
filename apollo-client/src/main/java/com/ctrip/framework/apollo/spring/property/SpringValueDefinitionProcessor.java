package com.ctrip.framework.apollo.spring.property;

import com.ctrip.framework.apollo.spring.util.SpringInjector;
import java.util.List;
import java.util.Set;

import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * To process xml config placeholders, e.g.
 *
 * <pre>
 *  &lt;bean class=&quot;com.ctrip.framework.apollo.demo.spring.xmlConfigDemo.bean.XmlBean&quot;&gt;
 *    &lt;property name=&quot;timeout&quot; value=&quot;${timeout:200}&quot;/&gt;
 *    &lt;property name=&quot;batch&quot; value=&quot;${batch:100}&quot;/&gt;
 *  &lt;/bean&gt;
 * </pre>
 *
 * https://www.cnblogs.com/kebibuluan/p/14432019.html
 *
 * 对所有的BeanDefinition进行遍历，將属性中包含${…}参数占位符的属性添加到Apollo 属性注册表。Apollo 属性注册表具体结构如下：
 *
 */
public class SpringValueDefinitionProcessor implements BeanDefinitionRegistryPostProcessor {

  private static final Multimap<String/*beanName*/,
      SpringValueDefinition/*具体的替换位置的详情信息*/> beanName2SpringValueDefinitions =
      LinkedListMultimap.create();
  private static final AtomicBoolean initialized = new AtomicBoolean(false);

  private final ConfigUtil configUtil;
  private final PlaceholderHelper placeholderHelper;

  public SpringValueDefinitionProcessor() {
    configUtil = ApolloInjector.getInstance(ConfigUtil.class);
    placeholderHelper = SpringInjector.getInstance(PlaceholderHelper.class);
  }

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    if (configUtil.isAutoUpdateInjectedSpringPropertiesEnabled()) {
      processPropertyValues(registry);
    }
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

  }

  public static Multimap<String, SpringValueDefinition> getBeanName2SpringValueDefinitions() {
    return beanName2SpringValueDefinitions;
  }

  private void processPropertyValues(BeanDefinitionRegistry beanRegistry) {
    if (!initialized.compareAndSet(false, true)) {
      // already initialized
      return;
    }

    String[] beanNames = beanRegistry.getBeanDefinitionNames();
    // 遍历所有的BeanDefinition
    for (String beanName : beanNames) {
      BeanDefinition beanDefinition = beanRegistry.getBeanDefinition(beanName);
      MutablePropertyValues mutablePropertyValues = beanDefinition.getPropertyValues();
      List<PropertyValue> propertyValues = mutablePropertyValues.getPropertyValueList();
      // 遍历当前beanDefinition中包含的 有${...} 的属性
      for (PropertyValue propertyValue : propertyValues) {
        Object value = propertyValue.getValue();
        if (!(value instanceof TypedStringValue)) {
          continue;
        }
        String placeholder = ((TypedStringValue) value).getValue();
        Set<String> keys = placeholderHelper.extractPlaceholderKeys(placeholder);

        if (keys.isEmpty()) {
          continue;
        }

        for (String key : keys) {
          beanName2SpringValueDefinitions.put(beanName,
              new SpringValueDefinition(key, placeholder, propertyValue.getName()));
        }
      }
    }
  }

  //only for test
  private static void reset() {
    initialized.set(false);
    beanName2SpringValueDefinitions.clear();
  }
}
