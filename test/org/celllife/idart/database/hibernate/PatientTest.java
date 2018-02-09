package org.celllife.idart.database.hibernate;

import java.util.Calendar;

import org.junit.Test;
import org.testng.Assert;

public class PatientTest {

	@Test
	public void testGetLatestAppointment() throws Exception{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		Appointment appointment1 = new Appointment();
		appointment1.setAppointmentDate(cal.getTime());
		cal.set(Calendar.MONTH, Calendar.FEBRUARY);
		Appointment appointment2 = new Appointment();
		appointment2.setAppointmentDate(cal.getTime());
		cal.set(Calendar.MONTH, Calendar.MARCH);
		Appointment appointment3 = new Appointment();
		appointment3.setAppointmentDate(cal.getTime());
		
		Patient patient = new Patient();
		patient.getAppointments().add(appointment2);
		patient.getAppointments().add(appointment3);
		patient.getAppointments().add(appointment1);

		Appointment latestAppointment = patient.getLatestAppointment();
		
		Assert.assertEquals(latestAppointment.getAppointmentDate(), appointment3.getAppointmentDate());
	}

	@Test
	public void testGetLatestAppointmentOneDate() throws Exception{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		Appointment appointment1 = new Appointment();
		appointment1.setAppointmentDate(cal.getTime());
		
		Patient patient = new Patient();
		patient.getAppointments().add(appointment1);

		Appointment latestAppointment = patient.getLatestAppointment();
		
		Assert.assertEquals(latestAppointment.getAppointmentDate(), appointment1.getAppointmentDate());
	}
	
	@Test
	public void testGetPreviousAppointmentOneDate() throws Exception{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		Appointment appointment1 = new Appointment();
		appointment1.setAppointmentDate(cal.getTime());
		
		Patient patient = new Patient();
		patient.getAppointments().add(appointment1);

		Appointment previousAppointment = patient.getPreviousAppointment();
		
		Assert.assertNull(previousAppointment);
	}

	@Test
	public void testGetPreviousAppointment() throws Exception{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		Appointment appointment1 = new Appointment();
		appointment1.setAppointmentDate(cal.getTime());
		cal.set(Calendar.MONTH, Calendar.FEBRUARY);
		Appointment appointment2 = new Appointment();
		appointment2.setAppointmentDate(cal.getTime());
		cal.set(Calendar.MONTH, Calendar.MARCH);
		Appointment appointment3 = new Appointment();
		appointment3.setAppointmentDate(cal.getTime());
		
		Patient patient = new Patient();
		patient.getAppointments().add(appointment2);
		patient.getAppointments().add(appointment3);
		patient.getAppointments().add(appointment1);

		Appointment previousAppointment = patient.getPreviousAppointment();
		
		Assert.assertEquals(previousAppointment.getAppointmentDate(), appointment2.getAppointmentDate());
	}
	
	@Test
	public void testGetLatestAppointmentNull() throws Exception{
		Patient patient = new Patient();
		Appointment latestAppointment = patient.getLatestAppointment();
		Assert.assertNull(latestAppointment);
	}
}
