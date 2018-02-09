package org.celllife.idart.integration.appointmentreminder;

import org.celllife.idart.database.hibernate.AppointmentReminder;
import org.celllife.idart.database.hibernate.Patient;

import java.io.Serializable;

/**
 * Patient entity used to communicate with the Appointment Reminder Service (ARS)
 */
public class PatientDto implements Serializable {

	private static final long serialVersionUID = -5083319649989279284L;

	String msisdn;
	String patientCode;
	boolean subscribed;

	public PatientDto() {
		
	}
	
	public static PatientDto build(AppointmentReminder appointmentReminder) {
		PatientDto dto = new PatientDto();
		
		Patient patient = appointmentReminder.getPatient();
		dto.setMsisdn(patient.getCellphone());
		dto.setSubscribed(appointmentReminder.isSubscribed());
		dto.setPatientCode(String.valueOf(patient.getPatientId()));

		return dto;
	}

	public String getMsisdn() {
		return msisdn;
	}

	/**
	 * Set the patient's cellphone number
	 * @param msisdn String msisdn starting with 27
	 */
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getPatientCode() {
		return patientCode;
	}
	
	/**
	 * Set the iDART specific patient code. Note that it will not be unique in the ARS, but will be unique
	 * when combined with the clinicCode - in other words it is unique for the clinic
	 *
	 * @param patientCode String patientCode
	 */
	public void setPatientCode(String patientCode) {
		this.patientCode = patientCode;
	}

	public boolean getSubscribed() {
		return subscribed;
	}
	
	/**
	 * Specifies whether the patient wishes to receive appointment reminder messages.
	 * To opt-out a patient from messages, set subscribed to false
	 * @param subscribed boolean true if they wish to receive messages
	 */
	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((msisdn == null) ? 0 : msisdn.hashCode());
		result = prime * result
				+ ((patientCode == null) ? 0 : patientCode.hashCode());
		result = prime * result + (subscribed ? 1231 : 1237);
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
		PatientDto other = (PatientDto) obj;
		if (msisdn == null) {
			if (other.msisdn != null)
				return false;
		} else if (!msisdn.equals(other.msisdn))
			return false;
		if (patientCode == null) {
			if (other.patientCode != null)
				return false;
		} else if (!patientCode.equals(other.patientCode))
			return false;
		if (subscribed != other.subscribed)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PatientDto [msisdn=" + msisdn + ", patientCode=" + patientCode
				+ ", subscribed=" + subscribed
				+ "]";
	}
}