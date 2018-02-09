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

/*
 * Created on 2005/03/17
 *
 */
package org.celllife.idart.database.hibernate;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.celllife.idart.utils.WorkingDaysUtil;

@Entity
public class Appointment {

    private static Log log = LogFactory.getLog(Appointment.class);

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "patient")
	private Patient patient;

	private Date appointmentDate;
	
	private Date visitDate;
	
	public static Comparator<Appointment> appointmentDateComparator = new Comparator<Appointment>() {
        @Override
        public int compare(Appointment a1, Appointment a2) {
            return a1.getAppointmentDate().compareTo(a2.getAppointmentDate());
        }
    };
    
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, mappedBy="appointment")
	private List<MessageSchedule> messageSchedules;

	public Appointment() {

		super();
	}

	/**
	 * Constructor for Appointment.
	 * @param patient Patient
     * @param appointmentDate
     * @param visitDate
	 */
	public Appointment(Patient patient, Date appointmentDate, Date visitDate) {
		super();
		this.patient = patient;
        setAppointmentDate(appointmentDate);
		this.visitDate = visitDate;
	}

	/**
	 * Method getAppointmentDate.
	 * @return Date
	 */
	public Date getAppointmentDate() {
		return appointmentDate;
	}

	/**
	 * Method setAppointmentDate.
	 * @param appointmentDate Date
	 */
	public void setAppointmentDate(Date appointmentDate) {
        if (appointmentDate != null) {
            try {
                while (!WorkingDaysUtil.isWorkingDay(appointmentDate)) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(appointmentDate);
                    calendar.add(Calendar.DATE,-1);
                    appointmentDate = calendar.getTime();
                }
            } catch (ParseException e) {
                log.warn("Could not parse file working_days.properties. Appointment reminders will be set for any day.");
            }
        }
		this.appointmentDate = appointmentDate;
	}
	
	/**
	 * Sets the appointment date without checking if the date is valid.
	 * @param overrideAppointmentDate
	 */
	public void overrideAppointmentDate(Date overrideAppointmentDate) {
		this.appointmentDate = overrideAppointmentDate;
	}

	/**
	 * Method getId.
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method setId.
	 * @param id int
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Method getPatient.
	 * @return Patient
	 */
	public Patient getPatient() {
		return patient;
	}

	/**
	 * Method setPatient.
	 * @param patient Patient
	 */
	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	/**
	 * @return the visitDate
	 */
	public Date getVisitDate() {
		return visitDate;
	}

	/**
	 * @param visitDate the visitDate to set
	 */
	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}

	/**
	 * Method to check if appointment is active
	 * @return
	 */
	public boolean isActive() {
		if(visitDate == null)
			return true;
		
		else return false;
	}

	public List<MessageSchedule> getMessageSchedules() {
		return messageSchedules;
	}

	public void setMessageSchedules(List<MessageSchedule> messageSchedules) {
		this.messageSchedules = messageSchedules;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((appointmentDate == null) ? 0 : appointmentDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((patient == null) ? 0 : patient.hashCode());
		result = prime * result
				+ ((visitDate == null) ? 0 : visitDate.hashCode());
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
		Appointment other = (Appointment) obj;
		if (appointmentDate == null) {
			if (other.appointmentDate != null)
				return false;
		} else if (!appointmentDate.equals(other.appointmentDate))
			return false;
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
		if (visitDate == null) {
			if (other.visitDate != null)
				return false;
		} else if (!visitDate.equals(other.visitDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Appointment [id=" + id + ", patient=" + patient
				+ ", appointmentDate=" + appointmentDate + ", visitDate="
				+ visitDate + "]";
	}
}