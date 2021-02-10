/*-
 * #%L
 * MATSim Episim
 * %%
 * Copyright (C) 2020 matsim-org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.matsim.run.modules;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.episim.EpisimConfigGroup;
import org.matsim.episim.VaccinationConfigGroup;
import org.matsim.episim.model.AgeDependentInfectionModelWithSeasonality;
import org.matsim.episim.model.AgeDependentProgressionModel;
import org.matsim.episim.model.ContactModel;
import org.matsim.episim.model.FaceMask;
import org.matsim.episim.model.InfectionModel;
import org.matsim.episim.model.ProgressionModel;
import org.matsim.episim.model.RandomVaccination;
import org.matsim.episim.model.SymmetricContactModel;
import org.matsim.episim.model.VaccinationModel;
import org.matsim.episim.policy.FixedPolicy;
import org.matsim.episim.policy.Restriction;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Scenario based on the Jakarta scenario.
 */
public class JakartaScenario extends AbstractModule {

	/**
	 * Activity names of the default params from {@link #addDefaultParams(EpisimConfigGroup)}.
	 */
	public static final String[] DEFAULT_ACTIVITIES = {
			"work", "education", "school", "shop",
			"leisure", "other", "outside",
			"freight_unloading", "freight_loading"
	};

	/**
	 * Adds default parameters for LA scenario.
	 */
	public static void addDefaultParams(EpisimConfigGroup config) {
		config.getOrAddContainerParams("pt", "tr");
		
		// regular out-of-home acts:
		config.getOrAddContainerParams("home").setContactIntensity(1.0);
		config.getOrAddContainerParams("work").setContactIntensity(1.47);
		config.getOrAddContainerParams("education").setContactIntensity(5.5);		
		config.getOrAddContainerParams("school").setContactIntensity(11.0); // many people, small space, no air exchange
		config.getOrAddContainerParams("shop").setContactIntensity(0.88);
		config.getOrAddContainerParams("leisure").setContactIntensity(9.24);
		config.getOrAddContainerParams("other").setContactIntensity(9.24); // ???
		config.getOrAddContainerParams("outside").setContactIntensity(1.0); // ???
		config.getOrAddContainerParams("freight_unloading").setContactIntensity(0.0); // ???
		config.getOrAddContainerParams("freight_loading").setContactIntensity(0.0); // ???




		// TODO: add contact intensities for further activity types!
		
		config.getOrAddContainerParams("quarantine_home").setContactIntensity(1.0);
	}

	@Provides
	@Singleton
	public Config config() {
		
		int sample = 1; // 1 = 1pct; 10 = 10pct; 25 = 25pct
		String scenarioLocation = "../shared-svn/projects/episim-jakarta/matsim-input-files/";

		Config config = ConfigUtils.createConfig(new EpisimConfigGroup());
		EpisimConfigGroup episimConfig = ConfigUtils.addOrGetModule(config, EpisimConfigGroup.class);

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");  
		LocalDateTime now = LocalDateTime.now();
		String dateTimeString = dtf.format(now);
		   
		config.controler().setOutputDirectory("output/output_" + sample + "pct_" + dateTimeString);
//		config.global().setCoordinateSystem("EPSG:3310");	
		
		if (sample == 1) {
			config.plans().setInputFile(scenarioLocation + "1pct/jakarta_population_withDistricts_reduced-for-episim.xml.gz");
			episimConfig.setInputEventsFile(scenarioLocation + "1pct/40.events_existing_1pct_reduced-for-episim.xml.gz");
			episimConfig.setSampleSize(0.01);
		}
		else if (sample == 10) {
			// TODO
		}
		else if (sample == 25) {
			// TODO	
		}
		else throw new RuntimeException("Sample size does not exist! Aborting...");
		
		episimConfig.setCalibrationParameter(2);		
		episimConfig.setFacilitiesHandling(EpisimConfigGroup.FacilitiesHandling.snz);
		episimConfig.setStartDate("2020-02-15");
		
		// Here we set the disease import.
		// First, set the day until which we have a disease import.
		// -> set to Integer.MAX_VALUE in order to not have a limitation
		episimConfig.setInitialInfections(Integer.MAX_VALUE);
		// Second, set the daily infected agents rates.
		// -> these numbers are given in infected agents per day
		// -> these numbers are daily numbers that are valid from provided start day
		// -> check sample size. If you set 5 here, in the 1pct scenario --> 500 infections per day
		Map<LocalDate, Integer> infectionsPerDay = new HashMap<>();
		infectionsPerDay.put(LocalDate.parse("2020-02-15"), 5);
		episimConfig.setInfections_pers_per_day(infectionsPerDay);
		
		addDefaultParams(episimConfig);
		
		// Here we set the restrictions. A possible starting point could be the google mobility reports: https://www.google.com/covid19/mobility/
		episimConfig.setPolicy(FixedPolicy.class, FixedPolicy.config()
				.restrict(LocalDate.parse("2020-03-15"), 0.85, DEFAULT_ACTIVITIES) //only 85% of out-of-home activities still occur
				.restrict(LocalDate.parse("2020-03-22"), 0.8, DEFAULT_ACTIVITIES)
				.restrict(LocalDate.parse("2020-03-29"), 0.75, DEFAULT_ACTIVITIES)
				.restrict(LocalDate.parse("2020-03-29"), 0, "education")			//example for closing and opening schools
				.restrict(LocalDate.parse("2020-05-29"), 1, "education")
				.restrict(LocalDate.parse("2020-06-10"), 0.9, DEFAULT_ACTIVITIES)

				//90% of public transport passengers wear a cloth mask 
				.restrict(LocalDate.parse("2020-04-01"), Restriction.ofMask(FaceMask.CLOTH, 0.9), "pt")
				.build()
		);
		
		VaccinationConfigGroup vaccinationConfigGroup = ConfigUtils.addOrGetModule(config, VaccinationConfigGroup.class);
		int totalCapacity = 50000;
		int vaccinationsPerDay = 2000;
		LocalDate startDateOfVaccinations = LocalDate.parse("2021-01-01");
		vaccinationConfigGroup.setEffectiveness(0.9);
		vaccinationConfigGroup.setDaysBeforeFullEffect(28);		
		vaccinationConfigGroup.setVaccinationCapacity_pers_per_day(Map.of(
					episimConfig.getStartDate(), 0,
					startDateOfVaccinations, (int) (vaccinationsPerDay),
					startDateOfVaccinations.plusDays(totalCapacity/vaccinationsPerDay), 0
					));
		

		return config;
	}
	
	@Override
	protected void configure() {
		bind(ContactModel.class).to(SymmetricContactModel.class).in(Singleton.class);
		bind(ProgressionModel.class).to(AgeDependentProgressionModel.class).in(Singleton.class);
		bind(InfectionModel.class).to(AgeDependentInfectionModelWithSeasonality.class).in(Singleton.class);
		bind(VaccinationModel.class).to(RandomVaccination.class).in(Singleton.class);
	}

}
