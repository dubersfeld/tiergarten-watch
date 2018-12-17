package com.dub.spring.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dub.spring.cluster.Cluster;
import com.dub.spring.cluster.DisplayCluster;

@Component
public class DisplayUtils {

	@Autowired
	private PortsHolder portsHolder; 
	
	public DisplayCluster clusterToDisplay(Cluster cluster) {
		
		List<Integer> ports = new ArrayList<>();
		List<String> processes = new ArrayList<>();
		
		for (Integer port : portsHolder.allocatedPorts) {
			ports.add(port);
			if (cluster.getProcessIds().containsKey(port)) {
				processes.add(cluster.getProcessIds().get(port));
			} else {
				processes.add("idle");
			}
		}
		
		return new DisplayCluster(ports, processes);
	}
}
