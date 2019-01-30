package com.dub.spring.stomp;

import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import com.dub.spring.cluster.ClusterMonitor;
import com.dub.spring.cluster.DisplayCluster;
import com.fasterxml.jackson.core.JsonProcessingException;

public class MyHandler extends StompSessionHandlerAdapter {
	
	@Autowired
	private ClusterMonitor clusterMonitor;
	
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
	       
		DisplayCluster displayCluster = clusterMonitor.getDisplayCluster();
		
		stompSession.send("/app/notify", displayCluster);
    }
}