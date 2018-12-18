package com.dub.spring.services;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.dub.spring.cluster.Cluster;
import com.dub.spring.cluster.DisplayCluster;
import com.dub.spring.utils.PortsHolder;
import com.dub.spring.utils.StreamGobbler;


public class ZooKeeperServiceImpl implements ZooKeeperService {
	
	@Value("${membershipRoot}")	
	private String membershipRoot;
	
	@Value("${clientUrl}")	
	private String clientUrl;
	
	@Autowired 
	private ZooKeeper zooKeeper;
	
	@Autowired 
	private PortsHolder portsHolder;
	
	public List<Integer> getActivePorts() throws KeeperException, InterruptedException {
		
		List<String> children = zooKeeper.getChildren(membershipRoot, false);
		
		List<Integer> activePorts = new ArrayList<>();
		
		for (String child : children) {
			String item = membershipRoot + "/" + child;	
			byte[] zoo_data = zooKeeper.getData(item, null, null);
			String data = new String(zoo_data);
			int index = data.indexOf('@');
			String port = data.substring(index + 1);
			activePorts.add(Integer.parseInt(port));	
		}
		
		for (Integer p : activePorts) {
			System.out.println(p);
		}
		
		return activePorts;
	}

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
	public Cluster getCluster() throws KeeperException, InterruptedException {
	
		List<String> children = zooKeeper.getChildren(membershipRoot, false);
			
		List<Integer> activePorts = new ArrayList<>();
		
		Map<Integer,String> activeProcesses = new HashMap<>();
		
		for (String child : children) {
			String item = membershipRoot + "/" + child;
			System.out.println(item);
			byte[] zoo_data = zooKeeper.getData(item, null, null);
			String data = new String(zoo_data);
			int index = data.indexOf('@');
			String port = data.substring(index + 1);
			String process = data.substring(0, index);
			activePorts.add(Integer.parseInt(port));
			activeProcesses.put(Integer.parseInt(port), process);
		}
				
		return new Cluster(activeProcesses);
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

	@Override
	public DisplayCluster getDisplayCluster() throws KeeperException, InterruptedException {
		
		List<String> children = zooKeeper.getChildren(membershipRoot, false);
		
		List<Integer> activePorts = new ArrayList<>();
		
		Map<Integer,String> activeProcesses = new HashMap<>();
		
		for (String child : children) {
			String item = membershipRoot + "/" + child;
			byte[] zoo_data = zooKeeper.getData(item, null, null);
			String data = new String(zoo_data);
			int index = data.indexOf('@');
			String port = data.substring(index + 1);
			String process = data.substring(0, index);
			activePorts.add(Integer.parseInt(port));
			activeProcesses.put(Integer.parseInt(port), process);
		}
			
		Cluster cluster = new Cluster(activeProcesses);
		
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
		DisplayCluster displayCluster = new DisplayCluster(ports, processes);
		
		return displayCluster;
	}
}
