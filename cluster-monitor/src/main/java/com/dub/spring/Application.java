package com.dub.spring;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import com.dub.spring.client.MyHandler;
import com.dub.spring.client.StompClient;
import com.dub.spring.cluster.ClusterMonitor;
import com.dub.spring.services.ZooKeeperService;
import com.dub.spring.services.ZooKeeperServiceImpl;
import com.dub.spring.watchers.ChildrenWatcher;
import com.dub.spring.watchers.ConnectionWatcher;

/**
 * Note here that some beans depend on other beans
 * The bean construction order is mandatory 
 * Otherwise the application fails due to a NullPointerException
 * */

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Value("${membershipRoot}")	
	private String membershipRoot;
	
	
	public static void main(String[] args)  {
		SpringApplication.run(Application.class, args);	
	}
	
	@Bean("zooKeeperService")
	public ZooKeeperService zooKeeperService() {
		return new ZooKeeperServiceImpl();
	}
	
	@Bean("connectionWatcher")
	public ConnectionWatcher connectionWatcher() {
		return new ConnectionWatcher();
	}
	
	@Bean("childrenWatcher")
	public ChildrenWatcher childrenWatcher() {
		return new ChildrenWatcher();
	}

	@Bean("myHandler")
	@DependsOn("zooKeeperService")
	public MyHandler myHandler() {
		return new MyHandler(zooKeeperService());
	}
	
	
	@Bean("stompClient")
	@DependsOn("myHandler")
	public StompClient stompClient() {
		StompClient stompClient = new StompClient(myHandler());	
		return stompClient;
	}
	
	@Bean
	@DependsOn("connectionWatcher")
	public ZooKeeper zooKeeper() throws IOException {
		
		System.out.println("Bean zooKeeper connectionWatcher == null "
			 + (connectionWatcher() == null));
		
		ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 2000, connectionWatcher());

		try {
			if (zooKeeper.exists(membershipRoot, false) == null) {
			zooKeeper.create(membershipRoot, "ClusterMonitorRoot".getBytes(), 
				Ids.OPEN_ACL_UNSAFE,  CreateMode.PERSISTENT);
			}
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
		
		return zooKeeper;
	}

	
	@Bean
	@DependsOn({"zooKeeper", "childrenWatcher"})	
	public ClusterMonitor clusterMonitor() throws IOException {
		
		ClusterMonitor clusterMonitor = new ClusterMonitor(zooKeeper(), childrenWatcher());
		return clusterMonitor;
	}


	@Override
	public void run(String... args) throws Exception {
		
		clusterMonitor().run();
		
	}
		
}