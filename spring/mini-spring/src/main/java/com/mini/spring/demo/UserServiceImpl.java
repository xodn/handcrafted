package com.mini.spring.demo;

import com.mini.spring.ioc.annotation.Autowired;
import com.mini.spring.ioc.annotation.Component;
import com.mini.spring.ioc.annotation.PostConstruct;
import com.mini.spring.ioc.bean.InitializingBean;

@Component
public class UserServiceImpl implements UserService, InitializingBean {

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        System.out.println("UserService @PostConstruct");
    }

    public void hello() {
        userRepository.save();
        System.out.println("hello world");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("InitializingBean.afterPropertiesSet");
    }
}
