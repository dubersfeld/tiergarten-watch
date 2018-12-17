package com.dub.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Only one web page in this application
 * */

@Controller
public class DefaultController {
	
	@RequestMapping({"/", "/index"})
	public String home(ModelMap model) {
		
		model.addAttribute("startServerUrl", "http://localhost:8089/startServer");
		model.addAttribute("stopServerUrl", "http://localhost:8089/stopServer");
		
		return "dashboard";
	}
	
	

}
