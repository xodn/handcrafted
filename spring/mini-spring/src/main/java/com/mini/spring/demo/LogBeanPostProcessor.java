package com.mini.spring.demo;

import com.mini.spring.ioc.bean.BeanPostProcessor;

import java.lang.reflect.Proxy;

public class LogBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("BeanPostProcessor before: " + beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("BeanPostProcessor after: " + beanName);
        if (bean instanceof UserService) {
            return Proxy.newProxyInstance(bean.getClass().getClassLoader(), bean.getClass().getInterfaces(),
                    (proxy, method, args) -> {
                        System.out.println("before");
                        Object result = method.invoke(bean, args);
                        System.out.println("after");
                        return result;
                    });
        }
        return bean;
    }
}
