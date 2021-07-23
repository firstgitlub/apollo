package com.ctrip.framework.apollo.spring.annotation;

import com.ctrip.framework.apollo.spring.property.SpringValueDefinitionProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import com.ctrip.framework.apollo.spring.config.PropertySourcesProcessor;
import com.ctrip.framework.apollo.spring.util.BeanRegistrationUtil;
import com.google.common.collect.Lists;

/**
 * @author Jason Song(song_s@ctrip.com)
 *
 * https://www.cnblogs.com/kebibuluan/p/14432019.html
 *
 * ImportBeanDefinitionRegistrar 接口的实现类作用于在 Spring 解析 Bean配置 生成 BeanDefinition 对象阶段，
 * 在 Spring 解析 Configuration注解 时，向 Spring容器 中增加额外的 BeanDefinition
 *
 */
public class ApolloConfigRegistrar implements ImportBeanDefinitionRegistrar {


  /**
   *
   *  期间主要是注册了下面这些 BeanDefinition
   *
   * 1、PropertySourcesPlaceholderConfigurer -------->BeanFactoryPostProcessor
   * 2、PropertySourcesProcessor -------->BeanFactoryPostProcessor
   * 3、ApolloAnnotationProcessor -------->BeanPostProcessor
   * 4、SpringValueProcessor -------->BeanFactoryPostProcessor和BeanPostProcessor
   * 5、SpringValueDefinitionProcessor-------->BeanDefinitionRegistryPostProcessor
   * （即BeanFactoryPostProcessor）
   * 6、ApolloJsonValueProcessor -------->BeanPostProcessor
   *
   * 这些类要么实现了BeanFactoryPostProcessor接口，要么实现了BeanPostProcessor接口，
   * 前面有提到过BeanFactoryPostProcessor和BeanPostProcessor是 Spring 提供的扩展机制，
   * BeanFactoryPostProcessor一定是在BeanPostProcessor之前执行
   *
   *
   */
  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata
        .getAnnotationAttributes(EnableApolloConfig.class.getName()));
    String[] namespaces = attributes.getStringArray("value");
    int order = attributes.getNumber("order");
    PropertySourcesProcessor.addNamespaces(Lists.newArrayList(namespaces), order);

    BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, PropertySourcesPlaceholderConfigurer.class.getName(),
        PropertySourcesPlaceholderConfigurer.class);

    BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, PropertySourcesProcessor.class.getName(),
        PropertySourcesProcessor.class);

    BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, ApolloAnnotationProcessor.class.getName(),
        ApolloAnnotationProcessor.class);

    BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, SpringValueProcessor.class.getName(), SpringValueProcessor.class);
    BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, SpringValueDefinitionProcessor.class.getName(), SpringValueDefinitionProcessor.class);

    BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, ApolloJsonValueProcessor.class.getName(),
            ApolloJsonValueProcessor.class);
  }
}
