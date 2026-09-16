package com.mini.spring.ioc.bean;

@FunctionalInterface
public interface ObjectFactory<T> {
    T getObject();
}
