package com.dub.spring.watchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.dub.spring.client.MyHandler;
import com.dub.spring.client.StompClient;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ChildrenWatcher implements Watcher {
	
	@Value("${membershipRoot}")	
	private String membershipRoot;
	
	boolean alive = true;
	
	@Autowired
	private ZooKeeper zooKeeper;
		
	@Autowired
	private StompClient stompClient;
	
	@Autowired
	private MyHandler myHandler;
	
	@Override
	public void process(WatchedEvent event) {
			
		if (event.getType() == Event.EventType.NodeChildrenChanged) {
			try {
				// Get current list of child znode
				// reset the watch
				List<String> children = zooKeeper.getChildren(membershipRoot, this);
				wall("!!!Cluster Membership Change!!!");
				wall("Members: " + children);
				System.out.println();
				List<Integer> activePorts = new ArrayList<>();
				Map<Integer,String> activeProcesses = new HashMap<>();
							
				for (String child : children) {
					String path = membershipRoot + "/" + child;
					System.out.println(path);
					byte[] zoo_data = zooKeeper.getData(path, this, null);
					String data = new String(zoo_data);
					int index = data.indexOf('@');
					String process = data.substring(0, index);
					String port = data.substring(index + 1);
					activeProcesses.put(Integer.valueOf(port), process);
					activePorts.add(Integer.parseInt(port));	
				}
										
				try {
					// publish here
					myHandler.sendCluster(stompClient.getStompSession());
					
				} catch (JsonProcessingException e) {				
					e.printStackTrace();
				}
				
			} catch (KeeperException e) {
				throw new RuntimeException(e);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				alive = false;
				throw new RuntimeException(e);
			} 
		}
	}// process


	public String getMembershipRoot() {
		return membershipRoot;
	}


	public void setMembershipRoot(String membershipRoot) {
		this.membershipRoot = membershipRoot;
	}


	//helper method
	public void wall(String message) {
		System.out.printf("\nMESSAGE: %s", message);
	}

}
