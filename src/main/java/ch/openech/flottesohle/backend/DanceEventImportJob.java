package ch.openech.flottesohle.backend;

import org.minimalj.backend.Backend;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import ch.openech.flottesohle.model.Location;

public class DanceEventImportJob implements Job {
	public static final JobKey JOB_KEY = new JobKey("Dance Event Import Job");

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		for (var entry : DanceEventProviders.PROVIDERS_BY_LOCATION_ID.entrySet()) {
			Location location = Backend.read(Location.class, entry.getKey());
			if (location.importStatus != null && location.importStatus.active != null && location.importStatus.active) {
				Backend.execute(entry.getValue());
			}
		}
	}

}
