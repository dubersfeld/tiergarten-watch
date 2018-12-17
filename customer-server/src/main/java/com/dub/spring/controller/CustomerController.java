package com.dub.spring.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dub.spring.domain.Customer;


@RestController
public class CustomerController {
	
	@Autowired
	private Environment environment;
	
	List<Customer> customers = new ArrayList<>();
	
	@PostConstruct
	public void init() {
		
		customers.add(new Customer("Donald", "Duck")); 
		customers.add(new Customer("Mickey", "Mouse")); 
		customers.add(new Customer("Roger", "Rabbit")); 
	}
	
	@RequestMapping("/allCustomers")
	public List<Customer> allCustomers() {
		return customers;
	}
	
	@RequestMapping("/allCustomersWithPort")
	public CustomersWithPort allCustomersWithPort() {
		Integer port = Integer.valueOf(environment.getProperty("server.port"));
		return new CustomersWithPort(customers, port);
	}
	

	private static class CustomersWithPort {
		
		List<Customer> customers;
		Integer port;
		
		public CustomersWithPort(List<Customer> customers, Integer port) {
			this.customers = customers;
			this.port = port;
		}
		
		public List<Customer> getCustomers() {
			return customers;
		}
		public void setCustomers(List<Customer> customers) {
			this.customers = customers;
		}
		public Integer getPort() {
			return port;
		}
		public void setPort(Integer port) {
			this.port = port;
		}	
	}
}