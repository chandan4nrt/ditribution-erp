package com.ecomm.nrt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api")
public class HelloController {

	@GetMapping("/hello")
	public String sayHello() {
		return "Hello NRT! Your Spring Boot server is running on port 8080.";
	}
}
