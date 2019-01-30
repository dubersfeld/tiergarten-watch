package com.dub.spring.listeners;


import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import com.dub.spring.stomp.StompClient;

@Component
public class StompConnectEventListener implements ApplicationListener<SessionConnectEvent> {
		
	@Autowired
	private StompClient stompClient;
			
	private boolean enable = true;
	
	@Override
	public void onApplicationEvent(SessionConnectEvent event) {
				
		System.out.println("onApplicationEvent begin "
				+ event.getClass());
		
		if (stompClient.getStompSession() == null) {
			
			try {
				stompClient.setStompSession(stompClient.connect().get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}					
		}
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}	
}
