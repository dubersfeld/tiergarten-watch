package com.dub.spring.services;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dub.spring.client.StompClient;
import com.dub.spring.cluster.Cluster;
import com.fasterxml.jackson.core.JsonProcessingException;


@Service
public class StompServiceImpl implements StompService {
	
	@Autowired
	private StompClient stompClient;
	
	@Override
	public void publishCluster(Cluster cluster) throws InterruptedException, ExecutionException, JsonProcessingException {
			
		stompClient.sendCluster(cluster);
	}
}
