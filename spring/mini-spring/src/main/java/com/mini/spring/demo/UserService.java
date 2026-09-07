package com.mini.spring.ioc.demo;

import com.mini.spring.ioc.annotation.Component;

@Component
public class UserService {
    public void hello() {
        System.out.println("hello world");
    }
}
