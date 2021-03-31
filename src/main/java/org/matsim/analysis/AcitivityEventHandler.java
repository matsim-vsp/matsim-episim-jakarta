package org.matsim.analysis;

import java.util.List;

import org.matsim.api.core.v01.events.ActivityStartEvent;
import org.matsim.api.core.v01.events.handler.ActivityStartEventHandler;

public class AcitivityEventHandler implements ActivityStartEventHandler{

	@Override
	public void handleEvent(ActivityStartEvent event) {
		List<String> activityTypes = FindActivityTypes.getActivityTypes();
		
		if (!activityTypes.contains(event.getActType()))
				activityTypes.add(event.getActType());
		
	}

}
