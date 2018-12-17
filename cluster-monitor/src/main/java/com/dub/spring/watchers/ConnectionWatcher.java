package com.dub.spring.watchers;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class ConnectionWatcher implements Watcher {
	
	@Override
	public void process(WatchedEvent event) {
		if (event.getType() == Watcher.Event.EventType.None
					&&
			event.getState() == Watcher.Event.KeeperState.SyncConnected) {
			System.out.printf("\nEvent Received: %s", event.toString());
		}	
	}
}
