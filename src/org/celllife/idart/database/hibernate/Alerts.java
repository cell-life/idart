package org.celllife.idart.database.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="alerts")
public class Alerts {
	
	public static final String ALERT_TYPE_ARS_SUBSCRIPTION = "Update Appointment Reminder subscription";
	public static final String ALERT_TYPE_ARS_REMINDER = "Schedule Appointment Reminder message";
	public static final String ALERT_TYPE_ARS_MISSED = "Schedule Appointment Reminder missed appointment message";
	public static final String ALERT_TYPE_ARS_UPDATE = "Update Appointment";
	public static final String ALERT_TYPE_ARS_DELETE = "Delete Appointment";

	@Id
	@GeneratedValue
	@Column(name="id", nullable=false, unique=true)
	private Integer id;
	
	@Column(name="alertmessage", nullable=false)
	private String alertMessage;
	
	@Column(name="alertdate", nullable=false)
	private Date alertDate;
	
	@Column(name="alerttype", nullable = false)
	private String alertType;
	
	@Column(name="void", nullable=false)
	private Boolean Void = false;
	
	@Column(name="retries")
	private Integer retries = new Integer(0);
	
	public Alerts() {
	}


	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}


	/**
	 * @return the alertMessage
	 */
	public String getAlertMessage() {
		return alertMessage;
	}


	/**
	 * @param alertMessage the alertMessage to set
	 */
	public void setAlertMessage(String alertMessage) {
		this.alertMessage = alertMessage;
	}


	/**
	 * @return the alertDate
	 */
	public Date getAlertDate() {
		return alertDate;
	}


	/**
	 * @param alertDate the alertDate to set
	 */
	public void setAlertDate(Date alertDate) {
		this.alertDate = alertDate;
	}


	/**
	 * @return the alertType
	 */
	public String getAlertType() {
		return alertType;
	}


	/**
	 * @param alertType the alertType to set
	 */
	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}


	/**
	 * Indicates that the alert is not longer valid (it has been deleted)
	 * @param _void
	 */
	public void setVoid(Boolean _void) {
		Void = _void;
	}


	/**
	 * See if the alert is currently an issue that needs to be attended to
	 * @return
	 */
	public Boolean getVoid() {
		return Void;
	}


	/**
	 * Determine how many retries have occurred as part of this alert
	 * @return
	 */
	public Integer getRetries() {
		return retries;
	}


	/**
	 * Set the number of retries associated with the alert. This is used in order to avoid creating an
	 * excessive number of alerts
	 * @param retries
	 */
	public void setRetries(Integer retries) {
		this.retries = retries;
	}

	/**
	 * Adds one to the current retries number
	 */
	public void incrementRetries() {
		this.retries++;
	}
}