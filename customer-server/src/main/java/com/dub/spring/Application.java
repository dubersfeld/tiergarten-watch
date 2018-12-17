package com.dub.spring;

import java.lang.management.ManagementFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.core.env.Environment;

import com.dub.spring.cluster.ClusterClient;

@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
public class Application implements CommandLineRunner {

	@Autowired
	private Environment environment;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		String hostPort = "localhost:2181";
		String name = ManagementFactory.getRuntimeMXBean().getName();
		int index = name.indexOf('@');
		Long processId = Long.parseLong(name.substring(0, index));
		String port = environment.getProperty("server.port");
		ClusterClient clusterClient = new ClusterClient(hostPort, processId, port);
		clusterClient.run();
	}
}
