package com.dub.spring.services;

import java.io.IOException;

public interface ProcessManagerService {
	
	public void startServer(int port) 
			throws IOException, InterruptedException;
	
	public void stopServer(String processId) 
			throws IOException, InterruptedException;
			
}
