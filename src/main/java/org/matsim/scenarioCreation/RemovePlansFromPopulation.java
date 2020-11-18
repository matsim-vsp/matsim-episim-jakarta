/* *********************************************************************** *
 * project: org.matsim.*
 * EditRoutesTest.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2020 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.scenarioCreation;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.scenario.ScenarioUtils;

/**
* @author smueller
*/

public class RemovePlansFromPopulation {	

	public static void main(String[] args) {
		
		Config config = ConfigUtils.createConfig();
		
		config.plans().setInputFile("../shared-svn/projects/episim-jakarta/matsim-input-files/1pct/jakarta_population.xml.gz");
		
		Scenario scenario = ScenarioUtils.loadScenario(config);
		
		for (Person person : scenario.getPopulation().getPersons().values()) {
			person.removePlan(person.getSelectedPlan());

		}
		
		PopulationUtils.writePopulation(scenario.getPopulation(), "../shared-svn/projects/episim-jakarta/matsim-input-files/1pct/jakarta_population_reduced-for-episim.xml.gz");
		
	}

}
