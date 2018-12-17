package com.dub.spring.cluster;

import java.util.ArrayList;
import java.util.List;

public class DisplayCluster {

	private List<Integer> ports;
	private List<String> processIds;
	
	public DisplayCluster() {
		this.ports = new ArrayList<>();
	}
	
	public DisplayCluster(List<Integer> ports, List<String> processIds) {
		this.processIds = processIds;
		this.ports = ports;
	}

	public List<Integer> getPorts() {
		return ports;
	}

	public void setPorts(List<Integer> ports) {
		this.ports = ports;
	}

	public List<String> getProcessIds() {
		return processIds;
	}

	public void setProcessIds(List<String> processIds) {
		this.processIds = processIds;
	}	
}
