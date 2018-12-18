package com.dub.spring.services;


import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;

import com.dub.spring.cluster.Cluster;
import com.dub.spring.cluster.DisplayCluster;

public interface ZooKeeperService {
	
	public List<Integer> getActivePorts() 
			throws KeeperException, InterruptedException;
	
	public Cluster getCluster()
			throws KeeperException, InterruptedException;
	
	public DisplayCluster getDisplayCluster()
			throws KeeperException, InterruptedException;
	
	public void startServer(int port) 
			throws IOException, InterruptedException;
	
	public void stopServer(String processId) 
			throws IOException, InterruptedException;
			
}
