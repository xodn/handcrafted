package com.mini.spring.demo;

import com.mini.spring.ioc.annotation.Autowired;
import com.mini.spring.ioc.annotation.Component;

@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void hello() {
        userRepository.save();
        System.out.println("hello world");
    }
}
