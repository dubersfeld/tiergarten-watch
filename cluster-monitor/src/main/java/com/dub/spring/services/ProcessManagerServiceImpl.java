package com.dub.spring.services;

import java.io.IOException;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dub.spring.utils.StreamGobbler;

@Service
public class ProcessManagerServiceImpl implements ProcessManagerService {

	@Value("${membershipRoot}")	
	private String membershipRoot;
	
	@Value("${clientUrl}")	
	private String clientUrl;
	
	@Override
	public void startServer(int port) throws IOException, InterruptedException {
		// start a new server
		
		ProcessBuilder builder = new ProcessBuilder();
				
		builder.command("bash", "-c", "cd " + clientUrl + " ; pwd ; ls ; source launch.sh " 
				+ port); 

		Process process = builder.start();
		    
		StreamGobbler streamGobbler = 
		    		  new StreamGobbler(process.getInputStream(), System.out::println);    		
		Executors.newSingleThreadExecutor().submit(streamGobbler);
	}

	@Override
	public void stopServer(String processId) throws IOException, InterruptedException {
		// stop a running server
		
		ProcessBuilder builder = new ProcessBuilder();
			
		// kill -15 is cleaner than kill -9 in this context
		builder.command("bash", "-c", "kill -15 " + processId); 
			      		
		Process process = builder.start();
				    		
		StreamGobbler streamGobbler = 
				    		  new StreamGobbler(process.getInputStream(), System.out::println);    		
				
		Executors.newSingleThreadExecutor().submit(streamGobbler);												
	}

}
