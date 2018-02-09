package model.manager;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.commonobjects.PropertiesManager;
import org.celllife.idart.database.hibernate.Alerts;
import org.celllife.idart.database.hibernate.Appointment;
import org.celllife.idart.database.hibernate.AppointmentReminder;
import org.celllife.idart.database.hibernate.Logging;
import org.celllife.idart.database.hibernate.MessageSchedule;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.integration.appointmentreminder.AppointmentReminderServiceManager;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.sms.SmsType;
import org.celllife.mobilisr.client.exception.RestCommandException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Manager that provides re-usable code for the AppointmentReminder functionality
 */
public class AppointmentReminderManager {
	
	private static Logger log = Logger.getLogger(AppointmentReminderManager.class);

    /**
     * Create the MessageSchedule messages (as configured in the SMS properties file) for the specified appointment.
     * 
     * @param appointment Appointment for which the messages must be created
     * @return List of MessageSchedules that are created
     */
    public static List<MessageSchedule> createAppointmentReminderMessage(Appointment appointment) {
    	
    	log.info("Creating appointment reminder MessageSchedule for Appointment: "+appointment);
    	
    	List<MessageSchedule> messages = new ArrayList<MessageSchedule>();
    	
    	// 1st Appointment reminder
    	int noOfDays = PropertiesManager.sms().appointmentReminderValue4();
    	if (noOfDays != -1) {
    		messages.add(createMessageSchedule(appointment, SmsType.MESSAGETYPE_APPOINTMENT_REMINDER, 4, noOfDays));
    	}
    	
    	// 2nd Appointment reminder
    	noOfDays = PropertiesManager.sms().appointmentReminderValue3();
    	if (noOfDays != -1) {
    		messages.add(createMessageSchedule(appointment, SmsType.MESSAGETYPE_APPOINTMENT_REMINDER, 3, noOfDays));
    	}

    	// 3rd Appointment reminder
    	noOfDays = PropertiesManager.sms().appointmentReminderValue2();
    	if (noOfDays != -1) {
    		messages.add(createMessageSchedule(appointment, SmsType.MESSAGETYPE_APPOINTMENT_REMINDER, 2, noOfDays));
    	}

    	// 4th Appointment reminder
    	noOfDays = PropertiesManager.sms().appointmentReminderValue1();
    	if (noOfDays != -1) {
    		messages.add(createMessageSchedule(appointment, SmsType.MESSAGETYPE_APPOINTMENT_REMINDER, 1, noOfDays));
    	}

    	return messages;
    }
    
    public static List<MessageSchedule> createAndSendMissedAppointmentMessages(boolean includeToday) {
    	List<MessageSchedule> messages = new ArrayList<MessageSchedule>();

		Transaction tx = null;
		Session hSession = null;
		try {
			hSession = HibernateUtil.getNewSession();
			tx = hSession.beginTransaction();

			// Get the old alerts
			List<Alerts> oldAlerts = AlertManager.getCurrentAlerts(Alerts.ALERT_TYPE_ARS_MISSED, hSession);
			
			// Find the appointments considered missed
			List<Appointment> appointments = AppointmentReminderManager.getMissedAppointments(hSession, includeToday);
			
			for (Appointment appointment: appointments) {
				// Create the appropriate missed appointment message
				MessageSchedule ms = AppointmentReminderManager.createMissedAppointmentMessage(hSession, appointment, includeToday);
				if (ms != null) {
					// If there is an existing missed appointment message that hasn't yet been sent, then mark them as alerts so they won't be sent
					AppointmentReminderManager.clearPendingMissedAppointmentMessages(hSession, appointment);
					// Save the new message
					hSession.save(appointment);
				}
			}
			
			tx.commit();
			tx = hSession.beginTransaction();
			
			// Get all the Missed Appointment pending MessageSchedules
			messages = AppointmentReminderManager.getMessagesToSchedule(hSession, SmsType.MESSAGETYPE_MISSED_APPOINTMENT);
			
			// Then we arrange the messages by appointment and send
			Map<Appointment, List<MessageSchedule>> messagesByAppointment = AppointmentReminderServiceManager.getInstance().organiseMessagesByAppointment(messages);
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

			// Clear old alerts
			AlertManager.expireAlerts(oldAlerts, hSession);
			
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
		
		return messages;
    }

    /**
     * Create the MessageSchedule messages (as configured in the SMS properties file) for the specified missed appointment
     * 
     * @param sess Session for Hibernate (must be open and started)
     * @param appointment Appointment for which the messages must be created
     * @param includeToday boolean true if todays missed appointments should be included (should be true if the user triggers the creation of missed appointment messages)
     * @return List of MessageSchedules that are created
     */
    public static MessageSchedule createMissedAppointmentMessage(Session sess, Appointment appointment, boolean includeToday) {
    	
    	// See how many days have elapsed since the appointment. 
    	// Note: not a 100% perfect solution, but will do - could have issues with day light savings transition days
    	int days = (int)(new Date().getTime() - appointment.getAppointmentDate().getTime()) / (1000 * 60 * 60 * 24);
    	
    	// Create an array containing the messages that have already been sent/scheduled
    	MessageSchedule[] messages = new MessageSchedule[] {
    			getMessageSchedule(sess, appointment, SmsType.MESSAGETYPE_MISSED_APPOINTMENT, 1),
    			getMessageSchedule(sess, appointment, SmsType.MESSAGETYPE_MISSED_APPOINTMENT, 2),
    			getMessageSchedule(sess, appointment, SmsType.MESSAGETYPE_MISSED_APPOINTMENT, 3),
    			getMessageSchedule(sess, appointment, SmsType.MESSAGETYPE_MISSED_APPOINTMENT, 4)
    	};

		// 4th Appointment Reminder
		int noOfDays = PropertiesManager.sms().appointmentMissedValue4();
		if (noOfDays != -1 && ((includeToday && days==(noOfDays-1)) || (!includeToday && days >= noOfDays))) {
			// check that the message hasn't already been sent
			if (messages[3] == null) {
				// create message if it hasn't already been sent
				return createMessageSchedule(appointment, SmsType.MESSAGETYPE_MISSED_APPOINTMENT, 4, noOfDays);
			}
		}
		
		// 3rd Appointment Reminder
		noOfDays = PropertiesManager.sms().appointmentMissedValue3();
		if (noOfDays != -1 && ((includeToday && days==(noOfDays-1)) || (!includeToday && days >= noOfDays))) {
			// check that the message (or a later message) hasn't already been sent
			if (messages[2] == null && messages[3] == null) {
				// create message if it hasn't already been sent
				return createMessageSchedule(appointment, SmsType.MESSAGETYPE_MISSED_APPOINTMENT, 3, noOfDays);
			}
		}
		
		// 2nd Appointment Reminder
		noOfDays = PropertiesManager.sms().appointmentMissedValue2();
		if (noOfDays != -1 && ((includeToday && days==(noOfDays-1)) || (!includeToday && days >= noOfDays))) {
			// check that the message (or later messages) hasn't already been sent
			if (messages[1] == null && messages[2] == null && messages[3] == null) {
				// create message if it hasn't already been sent
				return createMessageSchedule(appointment, SmsType.MESSAGETYPE_MISSED_APPOINTMENT, 2, noOfDays);
			}
		}

    	// 1st Appointment Reminder
    	noOfDays = PropertiesManager.sms().appointmentMissedValue1();
		if (noOfDays != -1 && ((includeToday && days==0) || (!includeToday && days >= noOfDays))) {
			// check that the message (or later messages) hasn't already been sent
			if (messages[0] == null && messages[1] == null && messages[2] == null && messages[3] == null) {
				// create message if it hasn't already been sent
				return createMessageSchedule(appointment, SmsType.MESSAGETYPE_MISSED_APPOINTMENT, 1, noOfDays);
			}
		}
		
		return null;
    }

    /**
     * Find a MessageSchedule given the appointment, messageType and messageNumber 
     * 
     * @param session Session for Hibernate (must be open and started)
     * @param appointment Appointment associated with the message schedule
     * @param messageType SmsType of the message (missed appointment or appointment reminder)
     * @param messageNumber int the index of the message (1-4)
     * @return
     */
    public static MessageSchedule getMessageSchedule(Session session, Appointment appointment, SmsType messageType, int messageNumber) {
    	MessageSchedule result = (MessageSchedule)session.createQuery(
    			"from MessageSchedule ms where ms.appointment = :appointment and ms.messageType = :messageType and ms.messageNumber = :messageNumber")
				.setInteger("appointment", appointment.getId())
				.setString("messageType", messageType.toString())
				.setInteger("messageNumber", messageNumber)
				.uniqueResult();
    	return result;
    }
    
    /**
     * Create a record of an appointment that was deleted (so it can be sent to ARS)
     * @param session Session for Hibernate (must be open and started)
     * @param appointment Appointment that was deleted
     */
    public static void savedDeletedAppointment(Session session, Appointment appointment) {
    	if (appointment.getPatient().getAppointmentReminder() != null && appointment.getPatient().getAppointmentReminder().isSubscribed()) {
			Logging appLogging = new Logging();
			appLogging.setIDart_User(LocalObjects.getUser(session));
			appLogging.setItemId(String.valueOf(appointment.getId()));
			appLogging.setModified('N');
			appLogging.setTransactionDate(new Date());
			appLogging.setTransactionType("Delete Appointment");
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
			appLogging.setMessage(sdf.format(appointment.getAppointmentDate()) + " - " + appointment.getPatient().getPatientId());
			session.save(appLogging);
    	}
    }

    /**
     * Process the records of deleted appointments and send to the ARS
     * @param session Session for Hibernate (must be open and started)
     */
    public static void sendDeletedAppointments(Session session) {
    	List<Alerts> oldDeleteAlerts = AlertManager.getCurrentAlerts(Alerts.ALERT_TYPE_ARS_DELETE, session);
    	
    	// Sorry for this hack, but retrieving this from the logs since the appointment has actually been deleted
    	@SuppressWarnings("unchecked")
		List<Logging> result = session.createQuery("select l from Logging l where l.transactionType = :transactionType and l.modified = :modified")
				.setString("transactionType", "Delete Appointment")
				.setCharacter("modified", 'N')
				.list();

    	for (Logging l : result) {
    		String[] message = l.getMessage().split("-");
    		if (message.length == 3) {
    			String appointmentDate = message[0].trim();
    			String appointmentTime = message[1].trim();
    			String patientId = message[2].trim();
				try {
					AppointmentReminderServiceManager.getInstance().deleteAppointment(session, patientId, appointmentDate, appointmentTime);
					l.setModified('Y'); // mark as processed
					session.save(l);
				} catch (RestCommandException e) {
					String errorMessage = MessageFormat.format(Messages.getString("appointmentreminders.alerts.missed"), patientId, appointmentDate);
					AlertManager.updateExistingOrCreateNewAlert(Alerts.ALERT_TYPE_ARS_DELETE, errorMessage, session, oldDeleteAlerts);
					log.error(errorMessage, e);
				}
    		} else {
    			log.error("Cannot process logging message for deleted appointment: " + l);
    		}
    	}

    	AlertManager.expireAlerts(oldDeleteAlerts, session);
    }
	
    /**
     * Retrieves a list of AppointmentReminder subscriptions that need to be updated
     * 
     * @param session Session for Hibernate (must be open and started)
     * @return List of AppointmentReminders
     */
	public static List<AppointmentReminder> getSubscriptionsToUpdate(Session session) {
		@SuppressWarnings("unchecked")
		List<AppointmentReminder> result = session.createQuery("select ar from AppointmentReminder ar " +
				"where ar.modified = :modified").setBoolean("modified", true).list();

		return result;
	}
	
	/**
	 * Retrieves a list of Messages that need to be scheduled (i.e. sent to the AppointmentReminderService)
	 * 
	 * @param session Session for Hibernate (must be open and started)
	 * @return List of MessageSchedules
	 */
	public static List<MessageSchedule> getMessagesToSchedule(Session session, SmsType messageType) {
		@SuppressWarnings("unchecked")
		List<MessageSchedule> result = session.createQuery("select ms from MessageSchedule ms " +
				"where ms.scheduledSuccessfully = :success and ms.messageType = :messageType and ms.sentToAlerts = false")
				.setBoolean("success", false)
				.setString("messageType", messageType.toString())
				.list();

		return result;
	}

	/**
	 * Retrieves a list of Appointments that were missed on a given day (but only for patients that have subscribed to appointment reminders)
	 *
	 * @param session Session for Hibernate (must be open and started)
	 * @param includeToday boolean true if todays missed appointments should be included (should be true if the user triggers the creation of missed appointment messages)
	 * @return List of Appointments, can be empty
	 */
	public static List<Appointment> getMissedAppointments(Session session, boolean includeToday) {
		
		Calendar cal = Calendar.getInstance();
		if (!includeToday) {
			cal.add(Calendar.DAY_OF_YEAR, -1);
		}
		Date endDate = cal.getTime();
		
		int noOfDays = PropertiesManager.sms().appointmentMissedValue4();
		cal.add(Calendar.DAY_OF_YEAR, -noOfDays);
		Date startDate = cal.getTime();
		
		log.info("Finding missed appointments between "+startDate+" and "+endDate);
		
		@SuppressWarnings("unchecked")
		List<Appointment> appointments = session.createQuery(
                "select a from Appointment a "
                        + "where a.patient.appointmentReminder is not null and a.patient.appointmentReminder.subscribed = true"
                        + " and date(a.appointmentDate) >= :startDate and date(a.appointmentDate) <= :endDate"
                        + " and a.visitDate is null")
                .setDate("startDate", startDate)
                .setDate("endDate", endDate)
                .list();

		return appointments;
	}
	
	/**
	 * Clears pending missed appointment messages for the specified Appointment. This is important for the situation where there is
	 * a communication problem with the ARS - we don't wish to sent multiple messages.
	 * 
	 * Clearing the message entails setting the 'sentToAlert' flag to true so it isn't picked up as a pending message to send.
	 * 
	 * @param session Session for Hibernate (must be open and started)
	 * @param appointment Appointment
	 * @return List of MessageSchedules that have been marked as invalid.
	 */
	public static List<MessageSchedule> clearPendingMissedAppointmentMessages(Session session, Appointment appointment) {
		@SuppressWarnings("unchecked")
		List<MessageSchedule> result = session.createQuery("select ms from MessageSchedule ms " +
				"where ms.scheduledSuccessfully = :success and ms.messageType = :messageType and ms.sentToAlerts = false and ms.appointment = :appointment")
				.setBoolean("success", false)
				.setString("messageType", SmsType.MESSAGETYPE_MISSED_APPOINTMENT.toString())
				.setEntity("appointment", appointment)
				.list();
		
		for (MessageSchedule ms : result) {
			ms.setSentToAlerts(true);
			session.save(ms);
		}

		return result;
	}
	
	/**
	 * Find all the messages scheduled successfully for a specified patient
	 */
	public static List<MessageSchedule> getMessagesSuccessfullyScheduled(Session session, Patient patient) {
		@SuppressWarnings("unchecked")
		List<MessageSchedule> result = session.createQuery("select ms from MessageSchedule ms " +
				"where ms.scheduledSuccessfully = :success"
				+ " and ms.appointment in (select a from Appointment a where a.patient = :patient)"
				+ " order by ms.scheduleDate desc")
				.setBoolean("success", true)
				.setEntity("patient", patient)
				.list();
		
		return result;
	}

	/**
	 * Find all the messages that haven't been scheduled successfully for a specified patient
	 */
	public static List<MessageSchedule> getMessagesNotYetScheduled(Session session, Patient patient) {
		@SuppressWarnings("unchecked")
		List<MessageSchedule> result = session.createQuery("select ms from MessageSchedule ms " +
				"where ms.scheduledSuccessfully = :success and ms.sentToAlerts = :alerts"
				+ " and ms.appointment in (select a from Appointment a where a.patient = :patient)"
				+ " order by ms.scheduleDate desc")
				.setBoolean("success", false)
				.setBoolean("alerts", false)
				.setEntity("patient", patient)
				.list();
		
		return result;
	}

	private static MessageSchedule createMessageSchedule(Appointment appointment, SmsType messageType, int messageNumber, int noOfDays) {
		MessageSchedule ms = new MessageSchedule();
		ms.setDaysToSchedule(noOfDays);
		ms.setMessageType(messageType);
		ms.setMessageNumber(messageNumber);
		AppointmentReminder subscription = appointment.getPatient().getAppointmentReminder();   
		ms.setLanguage(subscription.getLanguage());
		switch (messageType) {
			case MESSAGETYPE_APPOINTMENT_REMINDER:
				ms.setDescription("SMS appointment reminder for patient " + noOfDays + " days before their appointment");
				ms.setScheduleDate(createReminderMessageScheduleDate(appointment, noOfDays));
				break;
			case MESSAGETYPE_MISSED_APPOINTMENT:
				ms.setDescription("SMS to patients who missed their appointment " + noOfDays + " days ago");
				ms.setScheduleDate(createMissedMessageScheduleDate(appointment, noOfDays));
				break;
		}
		ms.setScheduledSuccessfully(false);
		ms.setAppointment(appointment);
		if (appointment.getMessageSchedules() == null) {
			appointment.setMessageSchedules(new ArrayList<MessageSchedule>());
		}
		appointment.getMessageSchedules().add(ms);
		return ms;
	}

    private static Date createReminderMessageScheduleDate(Appointment appointment, int dayBefore) {
    	Calendar appointmentDate = Calendar.getInstance();
    	appointmentDate.setTime(appointment.getAppointmentDate());
    	
    	// set correct time
    	AppointmentReminder subscription = appointment.getPatient().getAppointmentReminder();   	
    	appointmentDate.set(Calendar.HOUR_OF_DAY, subscription.getMessageTimeHours());
    	appointmentDate.set(Calendar.MINUTE, subscription.getMessageTimeMinutes());
    	appointmentDate.set(Calendar.SECOND, 0);
    	appointmentDate.set(Calendar.MILLISECOND, 0);
    	
    	// adjust for how many days before
		appointmentDate.roll(Calendar.DAY_OF_YEAR, -dayBefore);
		
		return appointmentDate.getTime();
    }

    private static Date createMissedMessageScheduleDate(Appointment appointment, int dayAfter) {
    	Calendar appointmentDate = Calendar.getInstance();
    	appointmentDate.setTime(appointment.getAppointmentDate());
    	
    	// set correct time
    	AppointmentReminder subscription = appointment.getPatient().getAppointmentReminder();   	
    	appointmentDate.set(Calendar.HOUR_OF_DAY, subscription.getMessageTimeHours());
    	appointmentDate.set(Calendar.MINUTE, subscription.getMessageTimeMinutes());
    	appointmentDate.set(Calendar.SECOND, 0);
    	appointmentDate.set(Calendar.MILLISECOND, 0);
    	
    	// adjust for how many days after (note this could be in the future for appointments that are missed today)
    	appointmentDate.roll(Calendar.DAY_OF_YEAR, dayAfter);
		
		return appointmentDate.getTime();
    }
}

