package com.buddy.buddy.account.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json")
public class UserController {

    @GetMapping("/hello_world")
    private String hello() {
        return "Hello World";
    }
}
