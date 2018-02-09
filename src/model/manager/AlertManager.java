package model.manager;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.Alerts;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class AlertManager {
	
	private static final Logger log = Logger.getLogger(AlertManager.class);

	/**
	 * Session expected to be open and have a transaction started.
	 * 
	 * @param type
	 * @param message
	 * @param hSession
	 */
	public static Alerts createAlert(String type, String message, Session hSession){
		Alerts alerts = null;
		try {
			alerts = new Alerts();
			alerts.setAlertDate(new Date());
			alerts.setAlertType(type);
			alerts.setAlertMessage(message);
			hSession.save(alerts);
		} catch (HibernateException e){
			log.error("Error creating alert", e);
		}
		return alerts;
	}
	
	/**
	 * Given a list of existing alerts, update an existing alert or create a new one depending on whether there is a match
	 * in the given list. The matching is done on alertType and alertMessage. If a match is found, the retries will be incremented
	 * and the alert will be removed from the list.
	 * 
	 * @param type String type of the message
	 * @param message String message description
	 * @param hSession Session hibernate session, must be open
	 * @param existingAlerts List of Alerts which will be used to find matches, can be null or empty
	 * @return Alert created or updated
	 */
	public static Alerts updateExistingOrCreateNewAlert(String type, String message, Session hSession, List<Alerts> existingAlerts) {
		Alerts existingAlert = null;
		if (existingAlerts != null) {
			for (Alerts a : existingAlerts) {
				// Try find a matching alert in the supplied list
				if (a.getAlertMessage().equals(message) && a.getAlertType().equals(type)) {
					existingAlert = a;
					break;
				}
			}
		}
		if (existingAlert == null) {
			// create a new alert if it goes pear shaped
			Alerts newAlert = AlertManager.createAlert(type, message, hSession);
			return newAlert;
		} else {
			// update an old alert
			if (existingAlerts != null) {
				existingAlerts.remove(existingAlert);
			}
			existingAlert.incrementRetries();
			hSession.save(existingAlert);
			return existingAlert;
		}
	}
	
	/**
	 * Goes through the list of Alerts and marks them as void.
	 * 
	 * @param oldAlerts List of Alerts to void
	 * @param hSession Session for Hibernate, must be open
	 */
	public static void expireAlerts(List<Alerts> oldAlerts, Session hSession) {
		for (Alerts a : oldAlerts) {
			a.setVoid(Boolean.TRUE);
			hSession.save(a);
		}
	}
	
	/**
	 * Get a list of non-voided Alerts of the specified type.
	 *
	 * @param type String alert type
	 * @param hSession Session for Hibernate, must be open
	 * @return List of Alerts found
	 */
	public static List<Alerts> getCurrentAlerts(String type, Session hSession) {
		@SuppressWarnings("unchecked")
		List<Alerts> result = hSession.createQuery(
				"from Alerts as a where a.alertType = :type and a.Void = :void")
				.setString("type", type)
				.setBoolean("void", Boolean.FALSE)
				.list();
		return result;
	}
	
	/**
	 * Get a list of non-voided Alerts of the specified type.
	 *
	 * @param type String alert type
	 * @return List of Alerts found
	 */
	public static List<Alerts> getCurrentAlerts(String type) {
		Session hSession = null;
		try {
			hSession = HibernateUtil.getNewSession();
			return getCurrentAlerts(type, hSession);
		} finally {
			if (hSession != null) {
				hSession.close();
			}
		}
	}
	

	/**
	 * Voids all Alerts of a specific type
	 *  
	 * @param hSession Session for Hibernate (must be open and started)
	 */
	public static void voidAllAlerts(String type, Session hSession) {
		hSession.createQuery("Update Alerts set void = :void where alerttype = :alerttype")
			.setBoolean("void", Boolean.TRUE)
			.setString("alerttype", type)
			.executeUpdate();
	}
	

	public static boolean hasCurrentAlerts(String type, Session hSession) {
		Long count = (Long) hSession.createQuery(
				"select count(a) from Alerts as a where a.alertType = :type and a.Void = :void")
				.setString("type", type)
				.setBoolean("void", Boolean.FALSE)
				.uniqueResult();
		return (count > 0);
	}

	public static boolean hasCurrentAlerts(String type) {
		Session hSession = null;
		try {
			hSession = HibernateUtil.getNewSession();
			return hasCurrentAlerts(type, hSession);
		} finally {
			if (hSession != null) {
				hSession.close();
			}
		}
	}
}
