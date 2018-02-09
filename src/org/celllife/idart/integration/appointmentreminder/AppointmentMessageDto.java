package org.celllife.idart.integration.appointmentreminder;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.celllife.idart.database.hibernate.MessageSchedule;
import org.celllife.idart.sms.SmsType;

/**
 * Appointment Message Entity used by iDART to communicate with the Appointment Reminder Service (ARS) 
 */
public class AppointmentMessageDto implements Serializable {

	private static final long serialVersionUID = -7081678385516247627L;
	
	enum MessageType { REMINDER, MISSED }

	String messageDate;
	String messageTime;
	String messageText;
	MessageType messageType;
	Integer messageSlot;

	public AppointmentMessageDto() {
		
	}
	
	public static AppointmentMessageDto build(String message, MessageSchedule messageSchedule) {
		AppointmentMessageDto messageDto = new AppointmentMessageDto();
		messageDto.setMessageDateTime(messageSchedule.getScheduleDate());
		messageDto.setMessageText(message);
		messageDto.setMessageSlot(messageSchedule.getMessageNumber());
        if (messageSchedule.getMessageType() == SmsType.MESSAGETYPE_APPOINTMENT_REMINDER)
            messageDto.setMessageType(MessageType.REMINDER);
        if (messageSchedule.getMessageType() == SmsType.MESSAGETYPE_MISSED_APPOINTMENT)
            messageDto.setMessageType(MessageType.MISSED);
		return messageDto;
	}

	public String getMessageDate() {
		return messageDate;
	}

	/**
	 * Specifies the date that the message must be sent.
	 * @param messageDate String date in format dd/MM/yyyy
	 */
	public void setMessageDate(String messageDate) {
		this.messageDate = messageDate;
	}

	public String getMessageTime() {
		return messageTime;
	}

	/**
	 * Specifies the time that the message must be sent
	 * @param messageTime String time in format HH:mm
	 */
	public void setMessageTime(String messageTime) {
		this.messageTime = messageTime;
	}

	/**
	 * Given a date object, sets the messageDate and messageTime attributes
	 * @param messageDateTime Date containing a timestamp of when the message must be sent 
	 */
	public void setMessageDateTime(Date messageDateTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		messageDate = sdf.format(messageDateTime);
		SimpleDateFormat stf = new SimpleDateFormat("HH:mm");
		messageTime = stf.format(messageDateTime);
	}

	public String getMessageText() {
		return messageText;
	}

	/**
	 * Specifies the content of the message to be sent to the patient regarding their appointment
	 * @param messageText String message content
	 */
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	/**
	 * Specifies the type of the message to be sent to the patient. It is either an appointment reminder
	 * or missed appointment message (if they did not come to their appointment).
	 * @param messageType MessageType
	 */
	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public Integer getMessageSlot() {
		return messageSlot;
	}

	/**
	 * Sets the slot of the message. The message slot is a sequential number
	 * indicating the order in which messages of a specific type are sent 
	 * @param messageSlot Integer message number
	 */
	public void setMessageSlot(Integer messageSlot) {
		this.messageSlot = messageSlot;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((messageDate == null) ? 0 : messageDate.hashCode());
		result = prime * result
				+ ((messageText == null) ? 0 : messageText.hashCode());
		result = prime * result
				+ ((messageTime == null) ? 0 : messageTime.hashCode());
		result = prime * result
				+ ((messageType == null) ? 0 : messageType.hashCode());
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
		AppointmentMessageDto other = (AppointmentMessageDto) obj;
		if (messageDate == null) {
			if (other.messageDate != null)
				return false;
		} else if (!messageDate.equals(other.messageDate))
			return false;
		if (messageText == null) {
			if (other.messageText != null)
				return false;
		} else if (!messageText.equals(other.messageText))
			return false;
		if (messageTime == null) {
			if (other.messageTime != null)
				return false;
		} else if (!messageTime.equals(other.messageTime))
			return false;
		if (messageType != other.messageType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AppointmentMessageDto [messageDate=" + messageDate
				+ ", messageTime=" + messageTime + ", messageText="
				+ messageText + ", messageType=" + messageType
				+ ", messageSlot=" + messageSlot + "]";
	}

}
