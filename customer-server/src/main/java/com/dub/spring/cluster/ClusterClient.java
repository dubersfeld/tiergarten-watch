package com.dub.spring.cluster;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ClusterClient implements Watcher, Runnable {

	private static String membershipRoot = "/Members";
	private ZooKeeper zk;
	
	public ClusterClient(String hostPort, Long pid, String port) {
		String processId = pid.toString();
		String data = processId + "@" + port;
		try {
			zk = new ZooKeeper(hostPort, 2000, this);
		} catch(IOException e) {
			e.printStackTrace();
		} 
		
		if (zk != null) {
			try {
				zk.create(membershipRoot + '/' + processId, data.getBytes(),
						Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			} catch (KeeperException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void close() {
		try {
			zk.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			synchronized(this) {
				while (true) {
					wait();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();	
		} finally {
			this.close();
		}
	}

	@Override
	public void process(WatchedEvent event) {
		System.out.printf("\nEventReceived: %s", event.toString());
	}

}
