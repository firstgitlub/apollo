package com.ctrip.framework.apollo.spring.property;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;

// 难道这个就是apollo属性注册表？？？

/**
 * Apollo配置中心动态生效机制，是基于Http长轮询请求和Spring扩展机制实现的，在Spring容器启动过程中，
 * Apollo通过自定义的BeanPostProcessor和BeanFactoryPostProcessor
 * 將参数中包含${…}占位符和@Value注解的Bean注册到Apollo框架中定义的注册表中。
 * 然后通过Http长轮询不断的去获取服务端的配置信息，一旦配置发生变化，Apollo会根据变化的配置的Key找到对应的Bean，
 * 然后修改Bean的属性，从而实现了配置动态生效的特性。
 *
 * 需要注意的是，Apollo在配置变化后，只能修改Bean的属性，例如我们数据源的属性发生变化，
 * 新创建的Connection对象是没问题的，但是连接池中已经创建的Connection对象相关信息是不能动态修改的，所以依然需要重启应用
 *
 *
 * https://www.cnblogs.com/kebibuluan/p/14432019.html
 *
 * https://www.cnblogs.com/kebibuluan/p/14435592.html
 *
 * 结合这两篇文章 即可完成 apollo 的实现原理完善
 *
 */
public class SpringValueRegistry {
  private final Multimap<String, SpringValue> registry = LinkedListMultimap.create();

  public void register(String key, SpringValue springValue) {
    registry.put(key, springValue);
  }

  public Collection<SpringValue> get(String key) {
    return registry.get(key);
  }
}
