package com.mini.spring.demo;

import com.mini.spring.ioc.context.ApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new ApplicationContext(UserService.class, UserRepository.class);

        UserService userService = (UserService) context.getBean("userService");

        userService.hello();
    }
}
