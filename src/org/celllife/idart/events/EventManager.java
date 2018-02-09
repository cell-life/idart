package org.celllife.idart.events;

import java.util.HashSet;
import java.util.Set;

import org.celllife.idart.integration.eKapa.EkapaEventListener;

import com.adamtaft.eb.EventBusService;

public class EventManager {

	private final Set<Object> participants = new HashSet<Object>();

	public void register() {
		participants.add(new EkapaEventListener());
		
		for (Object part : participants) {
			EventBusService.subscribe(part);
		}
	}

	public void deRegister() {
		int timeout = 0;
		while (timeout < 3 && EventBusService.hasPendingEvents()){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ignore) {}
		}
		
		for (Object part : participants) {
			EventBusService.unsubscribe(part);
		}
	}
	
	

}
