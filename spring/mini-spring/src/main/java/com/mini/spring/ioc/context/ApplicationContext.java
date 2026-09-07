package com.mini.spring.ioc.context;

import com.mini.spring.ioc.annotation.Autowired;
import com.mini.spring.ioc.annotation.Component;
import com.mini.spring.bean.BeanDefinition;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {
    private final Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

    private final Map<String, Object> singletonObjects = new HashMap<>();

    public ApplicationContext(Class<?>... componentClasses) {
        registerBeanDefinitions(componentClasses);
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
        return singletonObjects.get(beanName);
    }

    private Object getBeanByType(Class<?> type) {
        for (Object bean : singletonObjects.values()) {
            if (type.isAssignableFrom(bean.getClass())) {
                return bean;
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

    /**
     * 存在实例化顺序问题，如果先创建UserService，那么就会注入失败
     */
    // private void createBeans() {
    //     for (Map.Entry<String, BeanDefinition> entry : beanDefinitions.entrySet()) {
    //         String beanName = entry.getKey();
    //         BeanDefinition beanDefinition = entry.getValue();
    //
    //         try {
    //             Object bean = beanDefinition.getBeanClass().getDeclaredConstructor().newInstance();
    //
    //             singletonObjects.put(beanName, bean);
    //         } catch (Exception e) {
    //             throw new RuntimeException(e);
    //         }
    //     }
    // }

    private void createBeans() {
        // 第一阶段：实例化所有 Bean
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitions.entrySet()) {
            try {
                Object bean = entry.getValue().getBeanClass().getDeclaredConstructor().newInstance();

                singletonObjects.put(entry.getKey(), bean);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // 第二阶段：属性注入
        for (Object bean : singletonObjects.values()) {
            populateBean(bean);
        }

    }
}
