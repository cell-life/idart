package org.celllife.idart.integration.appointmentreminder;

import org.celllife.idart.database.hibernate.Appointment;
import org.celllife.idart.database.hibernate.AppointmentReminder;
import org.celllife.idart.database.hibernate.MessageSchedule;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.sms.SmsType;
import org.celllife.idart.test.HibernateTest;
import org.celllife.mobilisr.client.exception.RestCommandException;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AppointmentReminderServiceManagerTest extends HibernateTest {

    @Ignore
    @Test
    public void testUpdatePatient() throws RestCommandException {

        Patient patient = new Patient();
        patient.setPatientId("1234");
        patient.setCellphone("27724194158");

        AppointmentReminder appointmentReminder = new AppointmentReminder();
        appointmentReminder.setLanguage("English");
        appointmentReminder.setMessageTime("08:00");
        appointmentReminder.setModified(false);
        appointmentReminder.setSubscribed(true);
        appointmentReminder.setPatient(patient);

        AppointmentReminderServiceManager.getInstance().updatePatient(getSession(),appointmentReminder);

    }

    @Ignore
    @Test
    public void testSendMessages() throws RestCommandException {

        Patient patient = new Patient();
        patient.setPatientId("1234");
        patient.setCellphone("27724194158");

        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(getThreeDaysLater());
        appointment.setPatient(patient);

        MessageSchedule messageSchedule = new MessageSchedule();
        messageSchedule.setAppointment(appointment);
        messageSchedule.setScheduleDate(getFiveMinutesLater());
        messageSchedule.setMessageType(SmsType.MESSAGETYPE_APPOINTMENT_REMINDER);
        messageSchedule.setMessageNumber(1);
        messageSchedule.setLanguage("English");

        List<MessageSchedule> messageSchedules = new ArrayList<MessageSchedule>();
        messageSchedules.add(messageSchedule);

        AppointmentReminderServiceManager.getInstance().sendMessages(getSession(), appointment, messageSchedules);

    }

    private Date getThreeDaysLater() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,3);
        return calendar.getTime();
    }

    private Date getFiveMinutesLater() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,5);
        return calendar.getTime();
    }

}
