package com.dub.spring.client;

import org.apache.zookeeper.KeeperException;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import com.dub.spring.cluster.DisplayCluster;
import com.dub.spring.services.ZooKeeperService;
import com.fasterxml.jackson.core.JsonProcessingException;

public class MyHandler extends StompSessionHandlerAdapter {
	
	private ZooKeeperService zooKeeperService;
	
	public MyHandler(ZooKeeperService zooKeeperService) {
		this.zooKeeperService = zooKeeperService;
	}
	
	@Override
    public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {     
		
		try {
			sendCluster(stompSession);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void sendCluster(StompSession stompSession) throws JsonProcessingException, KeeperException, InterruptedException {
	       
		DisplayCluster displayCluster = zooKeeperService.getDisplayCluster();
		
		stompSession.send("/app/notify", displayCluster);
    }
}