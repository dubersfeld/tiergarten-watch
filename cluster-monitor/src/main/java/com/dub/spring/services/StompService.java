package com.dub.spring.services;

import java.util.concurrent.ExecutionException;

import com.dub.spring.cluster.Cluster;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface StompService {
	
	public void publishCluster(Cluster cluster)
				throws InterruptedException, ExecutionException, JsonProcessingException;
	

}
