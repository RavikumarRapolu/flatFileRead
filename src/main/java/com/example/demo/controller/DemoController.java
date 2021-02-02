package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String getHello() {
		return "hello";
	}
	
	@RequestMapping(value = "/generate", method = RequestMethod.GET)
	public String getRead() {
		return "hello";
	}
}
