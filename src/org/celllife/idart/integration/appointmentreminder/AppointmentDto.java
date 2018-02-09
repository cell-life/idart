package org.celllife.idart.integration.appointmentreminder;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.celllife.idart.commonobjects.PropertiesManager;
import org.celllife.idart.database.hibernate.Appointment;
import org.celllife.idart.database.hibernate.MessageSchedule;

/**
 * Appointment Entity used during communication with the Appointment Reminder Service (ARS)
 */
public class AppointmentDto implements Serializable {

	private static final long serialVersionUID = 115920795830328290L;
	
	private static Log log = LogFactory.getLog(AppointmentDto.class);

	String appointmentDate;
	String appointmentTime;
	boolean attended = false;
	
	List<AppointmentMessageDto> messages;

	public AppointmentDto() {
		
	}
	
	public static AppointmentDto build(Appointment appointment, List<MessageSchedule> messages) {
		AppointmentDto appointmentDto = new AppointmentDto();
        appointmentDto.setAppointmentDateTime(appointment.getAppointmentDate());
        if (appointment.getVisitDate() != null) {
        	appointmentDto.setAttended(true);
        }
		if (messages != null) {
			List<AppointmentMessageDto> dtos = new ArrayList<AppointmentMessageDto>();
			for (MessageSchedule m : messages) {
				int messageNumber = m.getMessageNumber();
				String messageLanguage = (m.getLanguage() == null || m.getLanguage().trim().isEmpty()) ? "English" : m.getLanguage();
				String propertyName = m.getMessageType().getPropertyName(messageNumber, messageLanguage);
				String mt = (String) PropertiesManager.smsRaw().get(propertyName);
				if (log.isDebugEnabled()) {
					log.debug("Message "+ messageNumber +" in " + messageLanguage + " " +  propertyName + ":" + mt);
				}
				dtos.add(AppointmentMessageDto.build(mt, m));
			}
			appointmentDto.setMessages(dtos);
		}
		return appointmentDto;
	}

	public String getAppointmentDate() {
		return appointmentDate;
	}

	/**
	 * Sets the date of the patient's appointment (in format dd/MM/yyyy)
	 * @param appointmentDate String containing the appointment date
	 */
	public void setAppointmentDate(String appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public String getAppointmentTime() {
		return appointmentTime;
	}

	/**
	 * Sets the time of the patient's appointment (in format HH:mm)
	 * @param appointmentTime String containing the appointment time
	 */
	public void setAppointmentTime(String appointmentTime) {
		this.appointmentTime = appointmentTime;
	}

	/**
	 * Given a date object, sets the appointmentDate and appointmentTime attributes
	 * @param appointmentDateTime Date containing a timestamp of when the message must be sent 
	 */
	public void setAppointmentDateTime(Date appointmentDateTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		appointmentDate = sdf.format(appointmentDateTime);
		SimpleDateFormat stf = new SimpleDateFormat("HH:mm");
		appointmentTime = stf.format(appointmentDateTime);
	}

	public List<AppointmentMessageDto> getMessages() {
		return messages;
	}

	/**
	 * Sets the messages to be sent to the patient regarding their appointment
	 * @param messages
	 */
	public void setMessages(List<AppointmentMessageDto> messages) {
		this.messages = messages;
	}

	public boolean isAttended() {
		return attended;
	}

	/**
	 * Indicates if the patient has attended their appointment or not
	 * @param attended boolean true if they have attended their appointment
	 */
	public void setAttended(boolean attended) {
		this.attended = attended;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((appointmentDate == null) ? 0 : appointmentDate.hashCode());
		result = prime * result
				+ ((appointmentTime == null) ? 0 : appointmentTime.hashCode());
		result = prime * result
				+ ((messages == null) ? 0 : messages.hashCode());
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
		AppointmentDto other = (AppointmentDto) obj;
		if (appointmentDate == null) {
			if (other.appointmentDate != null)
				return false;
		} else if (!appointmentDate.equals(other.appointmentDate))
			return false;
		if (appointmentTime == null) {
			if (other.appointmentTime != null)
				return false;
		} else if (!appointmentTime.equals(other.appointmentTime))
			return false;
		if (messages == null) {
			if (other.messages != null)
				return false;
		} else if (!messages.equals(other.messages))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AppointmentDto [appointmentDate=" + appointmentDate
				+ ", appointmentTime=" + appointmentTime + ", attended="
				+ attended + ", messages=" + messages + "]";
	}

}