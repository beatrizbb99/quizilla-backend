package de.hsrm.quiz_gateway.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class HelloController {
    
    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello! Server Works!!!";
    }
    
}
