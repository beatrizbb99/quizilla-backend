package de.hsrm.quiz_gateway.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@CrossOrigin(origins = "http://34.149.22.243")
public class HelloController {
    
    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello! Server Works!!! v1";
    }
    
}
