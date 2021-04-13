package org.matsim.analysis;

import java.util.ArrayList;
import java.util.List;

import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;

public class FindActivityTypes {

	private final static List<String> activityTypes = new ArrayList<String>();
	
	public static void main(String[] args) {
		String inputFileEvents = "../shared-svn/projects/episim-jakarta/matsim-input-files/10pct/40.events_10pct_reduced-for-episim.xml.gz";


		
		EventsManager events = EventsUtils.createEventsManager();

		AcitivityEventHandler activityEventHandler = new AcitivityEventHandler();

		events.addHandler(activityEventHandler);

		MatsimEventsReader reader = new MatsimEventsReader(events);

		reader.readFile(inputFileEvents);
		
		System.out.print(activityTypes.toString());
	}
	static List<String> getActivityTypes() {
		return activityTypes;
	}
}
