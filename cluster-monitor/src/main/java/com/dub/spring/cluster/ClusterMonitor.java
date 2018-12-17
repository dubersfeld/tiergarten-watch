package com.dub.spring.cluster;


import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;

import com.dub.spring.watchers.ChildrenWatcher;


public class ClusterMonitor implements Runnable {
	
	@Value("${membershipRoot}")	
	private String membershipRoot;

	boolean alive = true;
	
	private ZooKeeper zooKeeper;
	
	private ChildrenWatcher childrenWatcher;
	
	@PostConstruct
	public void init() {
		List<String> children;
		try {
			children = zooKeeper.getChildren(membershipRoot, childrenWatcher);
			System.err.println("Members: " + children);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
	}
		
	// main constructor		
	public ClusterMonitor(ZooKeeper zooKeeper, ChildrenWatcher childrenWatcher) {
		
		this.zooKeeper = zooKeeper;
		this.childrenWatcher = childrenWatcher;	
	}// constructor

	public synchronized void close() {
		try {
			zooKeeper.close();
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

	
}

