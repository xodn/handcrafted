package com.mini.spring.demo;

import com.mini.spring.ioc.annotation.Component;

@Component
public class UserRepository {
    public void save() {
        System.out.println("save user");
    }
}
