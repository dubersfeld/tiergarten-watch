package com.dub.spring.cluster;

import java.util.HashMap;
import java.util.Map;

public class Cluster {

	private Map<Integer,String> processIds;
		
	public Cluster() {
		this.processIds = new HashMap<>();
	}
	
	public Cluster(Map<Integer,String> processIds) {
		this.processIds = processIds;
	}
	
	public Map<Integer, String> getProcessIds() {
		return processIds;
	}

	public void setProcessIds(Map<Integer, String> processIds) {
		this.processIds = processIds;
	}

}
