package model.manager;

import java.util.Calendar;
import java.util.List;

import org.celllife.idart.database.hibernate.Appointment;
import org.celllife.idart.database.hibernate.AppointmentReminder;
import org.celllife.idart.database.hibernate.MessageSchedule;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.sms.SmsType;
import org.celllife.idart.test.HibernateTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AppointmentReminderManagerTest extends HibernateTest  {

	@Test
	public void testOneMissedAppointment() throws Exception {
		createPatient("arsTest1234", true, true);
		
		List<Appointment> appointments = AppointmentReminderManager.getMissedAppointments(getSession(), false);
		
		Assert.assertNotNull(appointments);
		Assert.assertEquals(appointments.size(), 1);
		
		for (Appointment a : appointments) {
			MessageSchedule ms = AppointmentReminderManager.createMissedAppointmentMessage(getSession(), a, false);
			Assert.assertEquals(ms.getMessageNumber(), 2);
			Assert.assertEquals(ms.getMessageType(), SmsType.MESSAGETYPE_MISSED_APPOINTMENT);
			Assert.assertNotNull(ms.getAppointment());
			Assert.assertNotNull(a.getMessageSchedules());
			Assert.assertEquals(a.getMessageSchedules().size(), 1);
		}
	}
	
	@Test
	public void testMissedAppointmentButNotSubscribed() throws Exception {
		createPatient("arsTest1234", true, false);
		
		List<Appointment> appointments = AppointmentReminderManager.getMissedAppointments(getSession(), false);
		
		Assert.assertNotNull(appointments);
		Assert.assertEquals(appointments.size(), 0);
	}
	
	private void createPatient(String patientId, boolean missedAppointment, boolean arsSubscribed) {
		
		Patient patient = new Patient();
		patient.setPatientId(patientId);
		patient.setClinic(AdministrationManager.getMainClinic(getSession()));
		patient.setSex('M');
		patient.setModified('T');
		
		AppointmentReminder arSubscription = new AppointmentReminder();
		arSubscription.setSubscribed(arsSubscribed);
		arSubscription.setPatient(patient);
		patient.setAppointmentReminder(arSubscription);
		Calendar appointmentDate = Calendar.getInstance();
		if (missedAppointment) {
			appointmentDate.roll(Calendar.DAY_OF_YEAR, -2);
		} else {
			appointmentDate.roll(Calendar.DAY_OF_YEAR, 2);
		}
		Appointment appointment = new Appointment(patient, appointmentDate.getTime(), null);
		patient.getAppointments().add(appointment);
		
		Integer id = (Integer)getSession().save(patient);
		
		getSession().flush();
		
		System.out.println("Created patient "+patient+" with id="+id);
		System.out.println("Created arSubscription "+arSubscription);
		System.out.println("Created appointment "+appointment);
	}
}
