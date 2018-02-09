/*
 * iDART: The Intelligent Dispensing of Antiretroviral Treatment
 * Copyright (C) 2006 Cell-Life
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License version
 * 2 for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package org.celllife.idart.database.hibernate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.apache.log4j.Logger;

/**
 * Domain Model Entity for Appointment Reminders - indicates that a user wishes to subscribe or unsubscribe
 * to appointment reminders and specifies how and when they would like to receive the messages. 
 */
@Entity
public class AppointmentReminder {
	
	private static Logger log = Logger.getLogger(AppointmentReminder.class);
	
	public final static String MESSAGE_TIME_FORMAT = "HH:mm";

	@Id
	@GeneratedValue
	private Integer id;
	
	/**
	 * Note: Cellphone number is stored in the patient
	 */
	@OneToOne
	@JoinColumn(name = "patient")
	Patient patient;	

	private boolean subscribed;
	private String messageTime;
	private String language;
	
	/** 
	 * Indicates that the appointment reminder details have been modified and thus should be communicated to the server.
	 * Once the subscription status has been updated on the server, then the modified flag is reset.
	 */
	private boolean modified;

	public AppointmentReminder() {
		super();
		this.id = -1;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the associated Patient
	 */
	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	/**
	 * Indicates if the patient wishes to receive appointment reminders. Note, the 
	 * cellphone number must be set correctly in the Patient object for this to work.
	 *
	 * @return true if the patient wishes to receive appointment reminders.
	 */
	public boolean isSubscribed() {
		return subscribed;
	}

	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
	}

	/**
	 * Indicates when the patient wishes to receive their message. Must be in the format 'HH:mm'
	 * @return
	 */
	public String getMessageTime() {
		return messageTime;
	}

	public void setMessageTime(String messageTime) {
		this.messageTime = messageTime;
	}

	/**
	 * Retrieves the AppointmentReminder messageTime hours part only.
	 * @return hour at which the message should be sent, will default to 8 if any errors are encountered or no messageTime set.
	 */
	public int getMessageTimeHours() {
		int hours = 8;
		if (this.messageTime != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(MESSAGE_TIME_FORMAT);
			try {
				Date time = sdf.parse(this.messageTime);
				Calendar cal = Calendar.getInstance();
				cal.setTime(time);
				hours = cal.get(Calendar.HOUR_OF_DAY);
			} catch (ParseException e) {
				log.warn("Could not parse AppointmentMessage messagetime '"+this.messageTime+"' (hours). Will default to "+hours+".", e);
			}
		}
		return hours;
	}

	/**
	 * Retrieves the AppointmentReminder messageTime minute part only.
	 * @return minute at which the message should be sent, will default to 30 if any errors are encountered or no messageTime set.
	 */
	public int getMessageTimeMinutes() {
		int minutes = 30;
		if (this.messageTime != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(MESSAGE_TIME_FORMAT);
			try {
				Date time = sdf.parse(this.messageTime);
				Calendar cal = Calendar.getInstance();
				cal.setTime(time);
				minutes = cal.get(Calendar.MINUTE);
			} catch (ParseException e) {
				log.warn("Could not parse AppointmentMessage messagetime '"+this.messageTime+"' (minutes). Will default to "+minutes+".", e);
			}
		}
		return minutes;
	}

	/**
	 * Get the language selected by the patient (configured in sms.properties)
	 */
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean isModified() {
		return modified;
	}

	/**
	 * Indicates that the appointment reminder details have been modified and thus should be communicated to the server.
	 * Note: Once the subscription status has been updated on the server, then the modified flag is reset.
	 */
	public void setModified(boolean modified) {
		this.modified = modified;
	}

	@Override
	public String toString() {
		return "AppointmentReminder [id=" + id + ", patient=" + patient + ", cellphone=" + patient.getCellphone()
				+ ", subscribed=" + subscribed + ", messageTime=" + messageTime + ", language=" + language
				+ ", modified=" + modified + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((patient == null) ? 0 : patient.hashCode());
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
		AppointmentReminder other = (AppointmentReminder) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (patient == null) {
			if (other.patient != null)
				return false;
		} else if (!patient.equals(other.patient))
			return false;
		return true;
	}

}
