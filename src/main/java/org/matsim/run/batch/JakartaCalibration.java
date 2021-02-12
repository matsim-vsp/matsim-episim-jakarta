package org.matsim.run.batch;

import com.google.inject.AbstractModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.episim.BatchRun;
import org.matsim.episim.EpisimConfigGroup;
import org.matsim.episim.TracingConfigGroup;
import org.matsim.episim.policy.FixedPolicy;
import org.matsim.episim.policy.FixedPolicy.ConfigBuilder;
import org.matsim.run.modules.JakartaScenario;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;



/**
 * Runs for jakarta model. To run all combinations set below in Params just start RunParallel.java.
 * Batch runs are useful to calibrate the model, to run multiple seeds or to investigate the effects of different restrictions.
 */
public class JakartaCalibration implements BatchRun<JakartaCalibration.Params> {

	@Override
	public AbstractModule getBindings(int id, @Nullable Params params) {
		return new JakartaScenario();
	}

	@Override
	public Metadata getMetadata() {
		return Metadata.of("ja", "calibration");
	}

	@Override
	public Config prepareConfig(int id, Params params) {

		JakartaScenario module = new JakartaScenario();
		Config config = module.config();
		config.global().setRandomSeed(params.seed);

		//adapt episimConfig here
		EpisimConfigGroup episimConfig = ConfigUtils.addOrGetModule(config, EpisimConfigGroup.class);
		episimConfig.setCalibrationParameter(params.calibrationParam);
		
		Map<LocalDate, Integer> infectionsPerDay = new HashMap<>();
		infectionsPerDay.put(LocalDate.parse(params.startDate1), params.dailyImportedCases1);
		infectionsPerDay.put(LocalDate.parse(params.startDate2), params.dailyImportedCases2);
		episimConfig.setInfections_pers_per_day(infectionsPerDay);

		//adapt tracingConfig here
		TracingConfigGroup tracingConfig = ConfigUtils.addOrGetModule(config, TracingConfigGroup.class);
		
		ConfigBuilder builder = FixedPolicy.parse(episimConfig.getPolicy());
		
		//adapt restrictions here
//		builder.restrict("2020-06-10", params.remainingFraction, OpenLosAngelesScenario.DEFAULT_ACTIVITIES);
		
		episimConfig.setPolicy(FixedPolicy.class, builder.build());

		return config;
	}

	public static final class Params {
		
		@GenerateSeeds(1)
		public long seed;
		
		@Parameter({1E-3, 1E-4})
		double calibrationParam;
		
		@StringParameter({"2020-02-15"})
		String startDate1;
		
		@IntParameter({1, 5, 10})
		int dailyImportedCases1;
		
		@StringParameter({"2020-04-01"})
		String startDate2;
		
		@IntParameter({0})
		int dailyImportedCases2;
		
//		@Parameter({0.75, 0.5})
//		double remainingFraction;
		
	}


}
