package com.dub.spring.cluster;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParseException;

import com.dub.spring.stomp.MyHandler;
import com.dub.spring.stomp.StompClient;
import com.dub.spring.utils.PortsHolder;
import com.fasterxml.jackson.core.JsonProcessingException;


public class ClusterMonitor implements Runnable {
	
	@Value("${membershipRoot}")	
	private String membershipRoot;

	boolean alive = true;
	
	boolean clusterAlive = true;
	
	private ZooKeeper zk;
			
	private final Watcher connectionWatcher;
	
	private final Watcher clusterWatcher;

	private Cluster cluster;
	
	List<String> memberIds;
	
	private PortsHolder portsHolder;
	
	private DisplayCluster displayCluster;// only visible field
	
	
	// main constructor		
	public ClusterMonitor(PortsHolder portsHolder, MyHandler myHandler, StompClient stompClient) throws IOException {
		
		this.portsHolder = portsHolder;
		
		// initial watcher
		connectionWatcher = new Watcher() {

			@Override
			public void process(WatchedEvent event) {
				if (event.getType() == Watcher.Event.EventType.None
						&&
					event.getState() == Watcher.Event.KeeperState.SyncConnected) {
					System.out.println("\nEvent Received: "
											+ event.toString());
				
					try {
						// set new watch using clusterWatcher
						memberIds = zk.getChildren("/Members", clusterWatcher);
																
						cluster = readCluster();
						
						setDisplayCluster();
						
						// here displayCluster is ready for Stomp display
								
					} catch (KeeperException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						clusterAlive = false;
					} catch (JsonParseException e) {			
						e.printStackTrace();
					} 
				}
			}
		};
		
		clusterWatcher = new Watcher() {

			@Override
			public void process(WatchedEvent event) {
				
				System.out.println("\nEvent Received: "
						+ event.toString());
				
				if (event.getType() == Event.EventType.NodeChildrenChanged) {
					
					try {
												
						// set new watch using clusterWatcher
						memberIds = zk.getChildren(membershipRoot, this);
																
						cluster = readCluster();
							
						setDisplayCluster();
							
						// display 
						if (stompClient.getStompSession() != null) {
							myHandler.sendCluster(stompClient.getStompSession());		
						}
							
					} catch (KeeperException | InterruptedException e) {
						
						e.printStackTrace();
					} catch (JsonProcessingException e) {
						
						e.printStackTrace();
					}
				}
			}
		};
		
		zk = new ZooKeeper("localhost:2181", 2000, connectionWatcher);	
		
	}// constructor

	public synchronized void close() {
		try {
			zk.close();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			synchronized(this) {
				while (alive) {
					wait();
				}
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		} finally {
			this.close();
		}
	}

	private Cluster readCluster() throws KeeperException, InterruptedException {
		
		Cluster cluster = new Cluster();
		
		for (String id : memberIds) {
			String item = "/Members" + "/" + id;
			byte[] node_data = zk.getData(item, null, null);
				
			String data = new String(node_data);
			int index = data.indexOf('@');
			String process = data.substring(0, index);
			String port = data.substring(index + 1);
					
			cluster.getProcessIds().put(Integer.valueOf(port), process);	
		}
		
		return cluster;
	}
	
	
	public DisplayCluster getDisplayCluster() {
		return displayCluster;
	}

	
	private void setDisplayCluster() {
				
		List<Integer> ports = new ArrayList<>();
		List<String> processes = new ArrayList<>();
				
		for (Integer port : portsHolder.getAllocatedPorts()) {
			ports.add(port);
			if (cluster.getProcessIds().containsKey(port)) {
				processes.add(cluster.getProcessIds().get(port));
			} else {
				processes.add("idle");
			}
		}
				
		displayCluster = new DisplayCluster(ports, processes);
	}
}