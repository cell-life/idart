package org.celllife.idart.database.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.celllife.idart.sms.SmsType;

/**
 * MessasgeSchedule contains a log of the Appointment Reminder messages to be scheduled/sent to the server
 */
@Entity
public class MessageSchedule {

	@Id
	@GeneratedValue
	@Column(name="id", nullable=false, unique=true)
	private Integer id;
	
	@Column(name="description", length=255)
	private String description;
	
	@Column(name="messagetype", nullable=false, length=255)
	@Enumerated(EnumType.STRING)
	private SmsType messageType;

	@Column(name="messagenumber", nullable=false)
	private int messageNumber;
	
	@Column(name="scheduledate", nullable=false)
	private Date scheduleDate;
	
	@Column(name="daystoschedule")
	private int daysToSchedule;
	
	@Column(name="scheduledsuccessfully")
	private boolean scheduledSuccessfully;
	
	@Column(name="senttoalerts")
	private boolean sentToAlerts;

	@Column(name="language")
	private String language;

	@OneToOne
	@JoinColumn(name = "appointment")
	Appointment appointment;
	
	public MessageSchedule() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of the message (note, this is not the message text, it is a log statement)
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the messageType
	 */
	public SmsType getMessageType() {
		return messageType;
	}

	/**
	 * Indicates the type of this message (MESSAGETYPE_MISSED_APPOINTMENT or MESSAGETYPE_APPOINTMENT_REMINDER)
	 * @param messageType the messageType to set
	 */
	public void setMessageType(SmsType messageType) {
		this.messageType = messageType;
	}

	/**
	 * The date that it was sent off to communicate or when the appointment reminder must send the message
	 * @return the scheduleDate
	 */
	public Date getScheduleDate() {
		return scheduleDate;
	}

	/**
	 * 
	 * @param scheduleDate the scheduleDate to set
	 */
	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	/**
	 * @return the daysToSchedule
	 */
	public int getDaysToSchedule() {
		return daysToSchedule;
	}

	/**
	 * Indicates the number of days before the appointment date or after the appointment date for missed appointments. This correlates to the messageNumber
	 * @param daysToSchedule the daysToSchedule to set
	 */
	public void setDaysToSchedule(int daysToSchedule) {
		this.daysToSchedule = daysToSchedule;
	}

	/**
	 * @return the scheduledSuccessfully
	 */
	public boolean isScheduledSuccessfully() {
		return scheduledSuccessfully;
	}

	/**
	 * Indicates that the messageSchedule successfully sent to the server (either communicate or ARS)
	 * @param scheduledSuccessfully the scheduledSuccessfully to set
	 */
	public void setScheduledSuccessfully(boolean scheduledSuccessfully) {
		this.scheduledSuccessfully = scheduledSuccessfully;
	}

	/**
	 * @return the messageText
	 */
	public int getMessageNumber() {
		return messageNumber;
	}

	/**
	 * Indicates which message "slot" (defined in the sms configuration)
	 * @param messageNumber the messageText to set
	 */
	public void setMessageNumber(int messageNumber) {
		this.messageNumber = messageNumber;
	}

	/**
	 * @return the sentToAlerts
	 */
	public boolean isSentToAlerts() {
		return sentToAlerts;
	}

	/**
	 * Indicates if the sending of this message schedule caused an alert to be generated.
	 * @param sentToAlerts boolean true if an alert has been created
	 */
	public void setSentToAlerts(boolean sentToAlerts) {
		this.sentToAlerts = sentToAlerts;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguage() {
		return language;
	}

	public Appointment getAppointment() {
		return appointment;
	}

	/**
	 * Sets the associated appointment for this message schedule. Each appointment can have many messages associated with it.
	 * @param appointment
	 */
	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((appointment == null) ? 0 : appointment.hashCode());
		result = prime * result + daysToSchedule;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((language == null) ? 0 : language.hashCode());
		result = prime * result + messageNumber;
		result = prime * result
				+ ((messageType == null) ? 0 : messageType.hashCode());
		result = prime * result
				+ ((scheduleDate == null) ? 0 : scheduleDate.hashCode());
		result = prime * result + (scheduledSuccessfully ? 1231 : 1237);
		result = prime * result + (sentToAlerts ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageSchedule other = (MessageSchedule) obj;
		if (appointment == null) {
			if (other.appointment != null)
				return false;
		} else if (!appointment.equals(other.appointment))
			return false;
		if (daysToSchedule != other.daysToSchedule)
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (messageNumber != other.messageNumber)
			return false;
		if (messageType != other.messageType)
			return false;
		if (scheduleDate == null) {
			if (other.scheduleDate != null)
				return false;
		} else if (!scheduleDate.equals(other.scheduleDate))
			return false;
		if (scheduledSuccessfully != other.scheduledSuccessfully)
			return false;
		if (sentToAlerts != other.sentToAlerts)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MessageSchedule [id=" + id + ", description=" + description
				+ ", messageType=" + messageType + ", messageNumber="
				+ messageNumber + ", scheduleDate=" + scheduleDate
				+ ", daysToSchedule=" + daysToSchedule
				+ ", scheduledSuccessfully=" + scheduledSuccessfully
				+ ", sentToAlerts=" + sentToAlerts + ", language=" + language
				+ ", appointment=" + (appointment != null ? appointment.getId() : "null") + "]";
	}
}
