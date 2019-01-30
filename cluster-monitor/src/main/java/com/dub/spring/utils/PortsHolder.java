package com.dub.spring.utils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

public class PortsHolder {
	
	@Value("${allocatedPorts}")
	private String aPorts;
	
	List<Integer> allocatedPorts = new ArrayList<>();;

	@PostConstruct
	public void init() {
		String[] ports = aPorts.split(",");
		for (int i = 0; i < ports.length; i++) {
			allocatedPorts.add(Integer.valueOf(ports[i]));
		}
		
		for (Integer port : allocatedPorts) {
			System.out.println(port);
		}
	}
	
	public List<Integer> getAllocatedPorts() {
		return allocatedPorts;
	}

	public void setAllocatedPorts(List<Integer> allocatedPorts) {
		this.allocatedPorts = allocatedPorts;
	}
	
}
