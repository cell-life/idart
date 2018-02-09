package org.celllife.idart.sms;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import model.manager.AlertManager;
import model.manager.AppointmentReminderManager;
import model.manager.PatientManager;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.Alerts;
import org.celllife.idart.database.hibernate.Appointment;
import org.celllife.idart.database.hibernate.AppointmentReminder;
import org.celllife.idart.database.hibernate.MessageSchedule;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.integration.appointmentreminder.AppointmentReminderServiceManager;
import org.celllife.idart.messages.Messages;
import org.celllife.mobilisr.client.exception.RestCommandException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job that updates patient AR subscriptions and sends the pending messages
 */
public class AppointmentReminderSchedulerJob extends CampaignSchedulingJob implements Job {

	private static final Logger log = Logger.getLogger(AppointmentReminderSchedulerJob.class.getName());
	
	public static final String JOB_NAME = "appointmentReminderJob";

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info(JOB_NAME+" scheduling starting");

		Transaction tx = null;
		Session hSession = null;
		try {
			hSession = HibernateUtil.getNewSession();
			tx = hSession.beginTransaction();

			// Firstly, we update patient subscriptions
			List<Alerts> oldAlerts = AlertManager.getCurrentAlerts(Alerts.ALERT_TYPE_ARS_SUBSCRIPTION, hSession);
			List<AppointmentReminder> subscriptions = AppointmentReminderManager.getSubscriptionsToUpdate(hSession);
			for (AppointmentReminder subscription : subscriptions) {
				try {
					AppointmentReminderServiceManager.getInstance().updatePatient(hSession, subscription);
					PatientManager.saveAppointmentReminder(hSession, subscription);
				} catch (RestCommandException e) {
					String errorMessage = MessageFormat.format(Messages.getString("appointmentreminders.alerts.subscription"), subscription.getPatient().getPatientId());
					AlertManager.updateExistingOrCreateNewAlert(Alerts.ALERT_TYPE_ARS_SUBSCRIPTION, errorMessage, hSession, oldAlerts);
					log.error(errorMessage, e);
				}
			}
			AlertManager.expireAlerts(oldAlerts, hSession);
			
			// Get all the MessageSchedules for Appointment Reminder that are pending
			List<MessageSchedule> messages = AppointmentReminderManager.getMessagesToSchedule(hSession, SmsType.MESSAGETYPE_APPOINTMENT_REMINDER);
			
			// Then we arrange the messages by appointment and send
			oldAlerts = AlertManager.getCurrentAlerts(Alerts.ALERT_TYPE_ARS_REMINDER, hSession);
			List<Alerts> oldUpdateAlerts = AlertManager.getCurrentAlerts(Alerts.ALERT_TYPE_ARS_UPDATE, hSession);
			Map<Appointment, List<MessageSchedule>> messagesByAppointment = AppointmentReminderServiceManager.getInstance().organiseMessagesByAppointment(messages);
			for (Appointment appointment : messagesByAppointment.keySet()) {
				List<MessageSchedule> appointmentMessages = messagesByAppointment.get(appointment);
				try {
					AppointmentReminderServiceManager.getInstance().sendMessages(hSession, appointment, appointmentMessages);
				} catch (RestCommandException e) {
					String errorMessage = MessageFormat.format(Messages.getString("appointmentreminders.alerts.reminder"), appointment.getPatient().getPatientId(), appointment.getAppointmentDate());
					AlertManager.updateExistingOrCreateNewAlert(Alerts.ALERT_TYPE_ARS_REMINDER, errorMessage, hSession, oldAlerts);
					log.error(errorMessage, e);
				}
				// Also notify that the patient did appear for their last appointment
				Patient patient = appointment.getPatient();
		    	Appointment previousAppointment = patient.getPreviousAppointment();
		    	if (previousAppointment != null) {
		    		if (previousAppointment.getVisitDate() == null) {
		    			log.warn("Found previous appointment " + previousAppointment + ", but the visitDate is null. Will discregard this appointment.");
		    		} else {
		    			try {
							AppointmentReminderServiceManager.getInstance().updateAppointment(hSession, previousAppointment);
						} catch (RestCommandException e) {
							String errorMessage = MessageFormat.format(Messages.getString("appointmentreminders.alerts.update"), appointment.getPatient().getPatientId(), previousAppointment.getAppointmentDate());
							AlertManager.updateExistingOrCreateNewAlert(Alerts.ALERT_TYPE_ARS_UPDATE, errorMessage, hSession, oldUpdateAlerts);
							log.error(errorMessage, e);
						}
		    		}
		    	}
			}
			AlertManager.expireAlerts(oldAlerts, hSession);
			AlertManager.expireAlerts(oldUpdateAlerts, hSession);
			
			// Get all the MessageSchedules for Missed Appointments that are pending
			messages = AppointmentReminderManager.getMessagesToSchedule(hSession, SmsType.MESSAGETYPE_MISSED_APPOINTMENT);
			
			// Then we arrange the messages by appointment and send
			oldAlerts = AlertManager.getCurrentAlerts(Alerts.ALERT_TYPE_ARS_MISSED, hSession);
			messagesByAppointment = AppointmentReminderServiceManager.getInstance().organiseMessagesByAppointment(messages);
			for (Appointment appointment : messagesByAppointment.keySet()) {
				List<MessageSchedule> appointmentMessages = messagesByAppointment.get(appointment);
				try {
					AppointmentReminderServiceManager.getInstance().sendMessages(hSession, appointment, appointmentMessages);
				} catch (RestCommandException e) {
					String errorMessage = MessageFormat.format(Messages.getString("appointmentreminders.alerts.missed"), appointment.getPatient().getPatientId(), appointment.getAppointmentDate());
					AlertManager.updateExistingOrCreateNewAlert(Alerts.ALERT_TYPE_ARS_MISSED, errorMessage, hSession, oldAlerts);
					log.error(errorMessage, e);
				}
			}
			AlertManager.expireAlerts(oldAlerts, hSession);
			
			// Process deleted Appointments
			AppointmentReminderManager.sendDeletedAppointments(hSession);
			
			tx.commit();

		} catch (Exception e) {
			log.error("Error submitting data to Appointment Reminder Service", e);
			if (tx != null) {
				tx.rollback();
			}
		} finally {
			if (hSession != null) {
				hSession.close();
			}

		}
	}
}