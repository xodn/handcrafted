package com.mini.spring.context;

import com.mini.spring.annotation.Component;
import com.mini.spring.bean.BeanDefinition;

import java.beans.Introspector;
import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {
    private final Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

    private final Map<String, Object> singleObjects = new HashMap<>();

    public ApplicationContext(Class<?>... componentClasses) {
        registerBeanDefinitions(componentClasses);
        createBeans();
    }

    public Object getBean(String beanName) {
        return singleObjects.get(beanName);
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

    private void createBeans() {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitions.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();

            try {
                Object bean = beanDefinition.getBeanClass().getDeclaredConstructor().newInstance();

                singleObjects.put(beanName, bean);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
