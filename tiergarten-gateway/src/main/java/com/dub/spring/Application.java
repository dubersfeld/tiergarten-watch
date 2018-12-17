package com.dub.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableZuulProxy 
public class Application {
	
	@LoadBalanced    
	@Bean
	public RestTemplate getRestTemplate() {
	        
		RestTemplate template = new RestTemplate();
	    
	    return template;    
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
