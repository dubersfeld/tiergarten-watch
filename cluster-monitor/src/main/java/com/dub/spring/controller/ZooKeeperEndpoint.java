package com.dub.spring.controller;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dub.spring.services.ProcessManagerService;


@RestController
public class ZooKeeperEndpoint {
	
	@Autowired 
	private ProcessManagerService processManagerService;
		
	@RequestMapping(value = "/startServer",
			method = RequestMethod.POST)
	public String startServer(@RequestBody PortForm portForm) {
				
		try {
			processManagerService.startServer(portForm.getPort());
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
			processManagerService.stopServer(processForm.getProcessId());
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
