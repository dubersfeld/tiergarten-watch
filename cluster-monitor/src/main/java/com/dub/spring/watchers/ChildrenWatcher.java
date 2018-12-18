package com.dub.spring.watchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.dub.spring.cluster.Cluster;
import com.dub.spring.services.StompService;
import com.dub.spring.services.ZooKeeperService;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ChildrenWatcher implements Watcher {
	
	@Value("${membershipRoot}")	
	private String membershipRoot;
	
	boolean alive = true;
	
	@Autowired
	private ZooKeeper zooKeeper;
	
	@Autowired
	private ZooKeeperService zooKeeperService;
	
	@Autowired
	private StompService stompService;
	
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
					String enclume = membershipRoot + "/" + child;
					System.out.println(enclume);
					byte[] zoo_data = zooKeeper.getData(enclume, this, null);
					String data = new String(zoo_data);
					int index = data.indexOf('@');
					String process = data.substring(0, index);
					String port = data.substring(index + 1);
					activeProcesses.put(Integer.valueOf(port), process);
					activePorts.add(Integer.parseInt(port));	
				}
								
				Cluster cluster = zooKeeperService.getCluster();
				
				try {
					// publish here
					stompService.publishCluster(cluster);
				} catch (JsonProcessingException | ExecutionException e) {				
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
