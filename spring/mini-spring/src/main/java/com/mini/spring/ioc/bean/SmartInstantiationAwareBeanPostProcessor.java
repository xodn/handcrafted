package com.mini.spring.ioc.bean;

public interface SmartInstantiationAwareBeanPostProcessor extends BeanPostProcessor {
    default Object getEarlyBeanReference(Object bean, String beanName) {
        return bean;
    }
}
