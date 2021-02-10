package org.matsim.scenarioCreation;

import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.population.PopulationUtils;

public class AddHomeToAttributes {

	final static String populationFile = "../shared-svn/projects/episim-jakarta/matsim-input-files/1pct/jakarta_population.xml.gz";
	final static String populationOutputFile = "../shared-svn/projects/episim-jakarta/matsim-input-files/1pct/jakarta_population_includingHomeCoord.xml.gz";

	public static void main(String[] args) {

		Population population = PopulationUtils.readPopulation(populationFile);

		for (Person person : population.getPersons().values()) {
			for (Plan plan : person.getPlans()) {
				for (PlanElement element : plan.getPlanElements()) {
					if (element instanceof Activity) {
						double x = ((Activity) element).getCoord().getX();
						double y = ((Activity) element).getCoord().getY();
						person.getAttributes().putAttribute("homeX", x);
						person.getAttributes().putAttribute("homeY", y);
						continue;
					}
				}
			}
			person.removePlan(person.getSelectedPlan());
		}
		PopulationUtils.writePopulation(population, populationOutputFile);
	}
}
