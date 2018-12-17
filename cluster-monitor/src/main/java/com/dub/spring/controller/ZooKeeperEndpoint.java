package com.dub.spring.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dub.spring.services.ZooKeeperService;

@RestController
public class ZooKeeperEndpoint {
		
	@Autowired
	private ZooKeeperService zooKeeperService;

	@RequestMapping("/activePorts")
	public List<Integer> getActivePorts() {
			
		try {
			List<Integer> activePorts = zooKeeperService.getActivePorts();
			return activePorts;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		
	}
	
	@RequestMapping(value = "/startServer",
			method = RequestMethod.POST)
	public String startServer(@RequestBody PortForm portForm) {
				
		try {
			zooKeeperService.startServer(portForm.getPort());
			return "STARTED";
		} catch (IOException e) {
			e.printStackTrace();
			return "ERROR";
		} catch (InterruptedException e) {
			e.printStackTrace();
			return "ERROR";
		}
		
	}
	
	@RequestMapping(value = "/stopServer",
			method = RequestMethod.POST)
	public String stopServer(@RequestBody ProcessForm processForm) {
				
		try {
			zooKeeperService.stopServer(processForm.getProcessId());
			return "STOPPED";
		} catch (IOException e) {
			e.printStackTrace();
			return "ERROR";
		} catch (InterruptedException e) {
			e.printStackTrace();
			return "ERROR";
		}
	}
}
