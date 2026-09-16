package com.mini.spring.ioc.bean;

public class BeanDefinition {
    private Class<?> beanClass;

    private String initMethodName;

    public BeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    public String getInitMethodName() {
        return this.initMethodName;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }
}
