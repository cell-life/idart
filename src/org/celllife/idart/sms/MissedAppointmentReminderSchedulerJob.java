package org.celllife.idart.sms;

import model.manager.AppointmentReminderManager;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job that schedules missed appointment messages for patients who did not turn up for their
 * appointments.
 * 
 * This job can be run daily (on 1st login) where it will check the previous days appointments
 * or it can be triggered from a menu button by the user at the end of the day (before they logout). 
 */
public class MissedAppointmentReminderSchedulerJob implements Job {

	private static final Logger log = Logger.getLogger(MissedAppointmentReminderSchedulerJob.class.getName());
	
	public static final String JOB_NAME = "missedAppointmentReminderJob";

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info(JOB_NAME+" scheduling starting");
		AppointmentReminderManager.createAndSendMissedAppointmentMessages(false);
	}
}