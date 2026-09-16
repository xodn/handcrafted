package com.mini.spring.ioc.context;

import com.mini.spring.demo.LogBeanPostProcessor;
import com.mini.spring.ioc.annotation.Autowired;
import com.mini.spring.ioc.annotation.Component;
import com.mini.spring.ioc.bean.*;
import com.mini.spring.ioc.annotation.PostConstruct;

import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ApplicationContext {
    private final Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

    private final Map<String, Object> singletonObjects = new HashMap<>();

    private final Map<String, Object> earlySingletonObjects = new HashMap<>();

    private final Map<String, ObjectFactory<?>> singletonObjectFactories = new HashMap<>();

    private final Map<String, Object> earlyBeanReferences = new HashMap<>();

    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public ApplicationContext(Class<?>... componentClasses) {
        registerBeanDefinitions(componentClasses);
        registerBeanPostProcessor();
        createBeans();
    }

    private void populateBean(Object bean) {
        Class<?> clazz = bean.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Autowired.class)) {
                continue;
            }

            Class<?> fieldType = field.getType();

            Object dependency = getBeanByType(fieldType);

            try {
                field.setAccessible(true);
                field.set(bean, dependency);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Object getBean(String beanName) {
        Object bean = singletonObjects.get(beanName);
        if (Objects.nonNull(bean)) {
            return bean;
        }

        bean = earlySingletonObjects.get(beanName);
        if (Objects.nonNull(bean)) {
            return bean;
        }

        ObjectFactory<?> factory = singletonObjectFactories.get(beanName);
        if (Objects.nonNull(factory)) {
            bean = factory.getObject();
            earlySingletonObjects.put(beanName, bean);
            singletonObjectFactories.remove(beanName);
            return bean;
        }

        BeanDefinition beanDefinition = beanDefinitions.get(beanName);

        return createBean(beanName, beanDefinition);
    }

    private Object getBeanByType(Class<?> type) {
        for (String beanName : beanDefinitions.keySet()) {
            BeanDefinition beanDefinition = beanDefinitions.get(beanName);
            if (type.isAssignableFrom(beanDefinition.getBeanClass())) {
                return getBean(beanName);
            }
        }

        throw new RuntimeException("No bean of type: " + type.getName());
    }

    private void registerBeanDefinitions(Class<?>... componentClasses) {
        for (Class<?> clazz : componentClasses) {
            Component component = clazz.getAnnotation(Component.class);

            if (component == null) {
                continue;
            }

            String beanName = component.value();

            if (beanName.isEmpty()) {
                beanName = Introspector.decapitalize(clazz.getSimpleName());
            }

            beanDefinitions.put(beanName, new BeanDefinition(clazz));
        }
    }

    private void registerBeanPostProcessor() {
        beanPostProcessors.add(new LogBeanPostProcessor());
    }

    private void createBeans() {
        for (String beanName : beanDefinitions.keySet()) {
            getBean(beanName);
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        // 实例化
        Object bean = instantiateBean(beanDefinition);

        // 提前暴露
        Object rawBean = bean;
        singletonObjectFactories.put(beanName, () -> getEarlyBeanReference(beanName, rawBean));

        // 属性注入
        populateBean(bean);

        // 初始化
        bean = initializeBean(bean, beanName);

        // 获取最终对象
        Object exposedObject = bean;

        // 如果循环依赖期间已经产生了早期对象，则使用早期对象
        Object earlyReference = earlyBeanReferences.get(beanName);
        if (Objects.nonNull(earlyReference)) {
            exposedObject = earlyReference;
        }

        // 放入一级缓存
        singletonObjects.put(beanName, exposedObject);

        // 清理二、三级缓存
        earlySingletonObjects.remove(beanName);
        singletonObjectFactories.remove(beanName);
        earlyBeanReferences.remove(beanName);

        return exposedObject;
    }

    private Object getEarlyBeanReference(String beanName, Object bean) {
        Object exposedObject = earlyBeanReferences.get(beanName);
        if (Objects.nonNull(exposedObject)) {
            return exposedObject;
        }

        exposedObject = bean;

        for (BeanPostProcessor processor : beanPostProcessors) {
            if (processor instanceof SmartInstantiationAwareBeanPostProcessor smartProcessor) {
                exposedObject = smartProcessor.getEarlyBeanReference(exposedObject, beanName);
            }
        }

        earlyBeanReferences.put(beanName, exposedObject);

        return exposedObject;
    }

    private Object instantiateBean(BeanDefinition beanDefinition) {
        try {
            Constructor<?> constructor = beanDefinition.getBeanClass().getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("创建bean失败： " + beanDefinition.getBeanClass(), e);
        }
    }

    private Object initializeBean(Object bean, String beanName) {
        for (BeanPostProcessor processor : beanPostProcessors) {
            bean = processor.postProcessBeforeInitialization(bean, beanName);
        }

        invokePostConstruct(bean);

        if (bean instanceof InitializingBean) {
            try {
                // 扩展点
                ((InitializingBean) bean).afterPropertiesSet();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        for (BeanPostProcessor processor : beanPostProcessors) {
            bean = processor.postProcessAfterInitialization(bean, beanName);
        }

        return bean;
    }

    private void invokePostConstruct(Object bean) {
        for (Method method : bean.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(PostConstruct.class)) {
                continue;
            }

            try {
                method.setAccessible(true);
                method.invoke(bean);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
