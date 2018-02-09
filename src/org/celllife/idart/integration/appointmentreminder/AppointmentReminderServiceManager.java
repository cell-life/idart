package org.celllife.idart.integration.appointmentreminder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.PropertiesManager;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.Appointment;
import org.celllife.idart.database.hibernate.AppointmentReminder;
import org.celllife.idart.database.hibernate.MessageSchedule;
import org.celllife.mobilisr.client.command.AbstractRestCommand;
import org.celllife.mobilisr.client.command.BasicAuthenticator;
import org.celllife.mobilisr.client.exception.RestCommandException;
import org.hibernate.Session;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;

/**
 * Implements the communication with the AppointmentReminderManager.
 * 
 * Note: re-using the handy Mobilisr/Communicate rest code, so requires the communicate-client jar.
 * We should consider putting that code into a cellphone rest client dependency (!)
 */
public class AppointmentReminderServiceManager {
	
	private static Logger log = Logger.getLogger(AppointmentReminderServiceManager.class);
    
    private static AppointmentReminderServiceManager instance;

    private Gson gson = new Gson();

    private BasicAuthenticator authenticator;
    
    public synchronized static AppointmentReminderServiceManager getInstance() {
        if (instance == null) {
            instance = new AppointmentReminderServiceManager(PropertiesManager.sms().appointmentRemindersUsername(), PropertiesManager.sms().appointmentRemindersPassword());
        }
        return instance;
    }

    public AppointmentReminderServiceManager(String username, String password) {
		this.authenticator = new BasicAuthenticator(username, password);
    }

    /**
     * Sends the updated patient subscription to the ARS. A successful submission will result in the
     * AppointmentReminder object being modified (to clear the modification flag).
     *
     * @param sess Session for Hibernate (must be open and started)
     * @param appointmentReminder AppointmentReminder subscription to save
     * @throws RestCommandException if any error occurs during the server communication
     */
	public void updatePatient(Session sess, AppointmentReminder appointmentReminder) throws RestCommandException {
		
		log.debug("Starting updating Patient's appointment reminder subscription. "+appointmentReminder);
		
		PatientDto patientDto = PatientDto.build(appointmentReminder);
		String relativeUrl = "/service/patient";

		log.info("About to perform a PUT on "+relativeUrl+" for patient "+patientDto);

        Map<String,Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("clinicCode",iDartProperties.clinicCode);
        queryParameters.put("patientCode",patientDto.getPatientCode());

        try {
	        JsonPutCommand command = new JsonPutCommand(PropertiesManager.sms().appointmentRemindersUrl(), relativeUrl, gson.toJson(patientDto));
	        command.setQueryParameters(queryParameters);
	        command.setAuthenticator(authenticator);
	        command.execute(String.class, Status.OK.getStatusCode());
        } catch (Exception e) {
        	throw new RestCommandException("Error while updating the ARS subscription for patient "+appointmentReminder.getPatient().getPatientId(),e);
        }

		appointmentReminder.setModified(false); // clear modification flag
		sess.save(appointmentReminder);
		
		log.debug("Finished updating Patient's appointment reminder subscription "+appointmentReminder);
	}
	
	/**
	 * Organise a group of MessageSchedules by Appointment. There are always a couple appointment reminder messages
	 * or missed appointment messages for each appointment.
	 * 
	 * This is required because the ARS requires that messages are sent by appointment.
	 * 
	 * @param messages List of MessageSchedules needing to be communicated, can be for multiple appointments
	 * @return Map of MessageSchedules indexed by the Appointment 
	 */
	public Map<Appointment, List<MessageSchedule>> organiseMessagesByAppointment(List<MessageSchedule> messages) {
		Map<Appointment, List<MessageSchedule>> messagesByAppointment = new HashMap<Appointment, List<MessageSchedule>>();
		
		for (MessageSchedule ms : messages) {
			Appointment appointment = ms.getAppointment();
			List<MessageSchedule> appointmentMessages = messagesByAppointment.get(appointment);
			if (appointmentMessages == null) {
				appointmentMessages = new ArrayList<MessageSchedule>();
				messagesByAppointment.put(appointment, appointmentMessages);
			}
			appointmentMessages.add(ms);
		}
		
		return messagesByAppointment;
	}

	/**
	 * Sends the appointment messages to the ARS. The messages must be for a single Appointment. 
	 * 
	 * A successful submission will result in the MessageSchedule entities to be modified (to indicate success or not).
	 *
	 * @param sess Session for Hibernate (must be open and started)
	 * @param appointment Appointment to create or update
	 * @param messages List of MessageSchedules needing to be communicated
	 * @throws RestCommandException if any error occurs during the server communication
	 */
	public void sendMessages(Session sess, Appointment appointment, List<MessageSchedule> messages) throws RestCommandException {
		
		log.debug("Starting to send appointment related messages "+messages);

		// convert to a DTO for the server communication
		AppointmentDto appointmentDto = AppointmentDto.build(appointment, messages);
		
		// send to the server
		String relativeUrl = "/service/appointment";
		log.info("About to perform a PUT on "+relativeUrl+" for appointment "+appointmentDto);

        Map<String,Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("clinicCode",iDartProperties.clinicCode);
        queryParameters.put("patientCode",appointment.getPatient().getPatientId());

        JsonPutCommand command = new JsonPutCommand(PropertiesManager.sms().appointmentRemindersUrl(), relativeUrl, gson.toJson(appointmentDto));
        command.setQueryParameters(queryParameters);
        command.setAuthenticator(authenticator);

        try {
        	String response = command.execute(String.class, Status.OK.getStatusCode());
        	log.debug("Received response "+response);
            for (MessageSchedule ms : messages) {
                ms.setScheduledSuccessfully(true);
                sess.update(ms);
            }
        } catch (Exception e) {
        	throw new RestCommandException("Error while sending messages to ARS for appointment "+appointment,e);
        }

		log.debug("Finished sending appointment related messages (successfully) "+messages);
	}
	
	/**
	 * Updates the information about the specified appointment.
	 *
	 * @param sess Session for Hibernate (must be open and started)
	 * @param appointment Appointment to update
	 * @throws RestCommandException if any error occurs during the server communication
	 */
	public void updateAppointment(Session sess, Appointment appointment) throws RestCommandException {
		
		log.debug("Starting to update appointment "+appointment);

		// convert to a DTO for the server communication
		AppointmentDto appointmentDto = AppointmentDto.build(appointment, null);
		
		// send to the server
		String relativeUrl = "/service/appointment";
		log.info("About to perform a PUT on "+relativeUrl+" for appointment "+appointmentDto);

        Map<String,Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("clinicCode",iDartProperties.clinicCode);
        queryParameters.put("patientCode",appointment.getPatient().getPatientId());

        JsonPutCommand command = new JsonPutCommand(PropertiesManager.sms().appointmentRemindersUrl(), relativeUrl, gson.toJson(appointmentDto));
        command.setQueryParameters(queryParameters);
        command.setAuthenticator(authenticator);

        try {
        	String response = command.execute(String.class, Status.OK.getStatusCode());
        	log.debug("Received response "+response);
        } catch (Exception e) {
        	throw new RestCommandException("Error while updating appointment on ARS. "+appointment,e);
        }

		log.debug("Finished updating appointment (successfully) "+appointment);
	}

	/**
	 * Deletes the specified appointment (and of course all its messages)
	 *
	 * @param sess Session for Hibernate (must be open and started)
	 * @param String patientId id of the patient whose appointment must be deleted
	 * @param String appointmentDate the date of the deleted appointment (dd/MM/yyyy)
	 * @param String appointmentTime the time of the deleted appointment (HH:mm) 
	 * @throws RestCommandException if any error occurs during the server communication
	 */
	public void deleteAppointment(Session sess, String patientId, String appointmentDate, String appointmentTime) throws RestCommandException {
		
		log.debug("Starting to delete appointment on " + appointmentDate + " for " + patientId);
		
		// send to the server
		String relativeUrl = "/service/appointment";
		log.info("About to perform a DELETE on "+relativeUrl+" for appointment on "+appointmentDate + " " + appointmentTime);

        Map<String,Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("clinicCode",iDartProperties.clinicCode);
        queryParameters.put("patientCode",patientId);
        queryParameters.put("appointmentDate", appointmentDate);
        queryParameters.put("appointmentTime", appointmentTime);

        JsonDeleteCommand command = new JsonDeleteCommand(PropertiesManager.sms().appointmentRemindersUrl(), relativeUrl);
        command.setQueryParameters(queryParameters);
        command.setAuthenticator(authenticator);

        try {
        	String response = command.execute(String.class, Status.OK.getStatusCode());
        	log.debug("Received response "+response);
        } catch (RestCommandException e) {
        	if (e.getStatusCode() == 404) {
        		log.warn("Received a 404 error while deleting an appointment on the ARS. Assuming that this is because the "
        				+ "appointment has not yet been sent to the ARS, so it will be ignored. Error: "+e.getMessage());
        	} else {
        		throw new RestCommandException("Error while deleting appointment on " + appointmentDate + " for " + patientId + " on ARS. ",e);
        	}
        } catch (Exception e) {
        	throw new RestCommandException("Error while deleting appointment on " + appointmentDate + " for " + patientId + " on ARS. ",e);
        }

        log.debug("Finished deleting appointment on " + appointmentDate + " for " + patientId + " (successfully)");
	}
}

//FIXME: This is a hack! Mobilisr client will only work with xml, so this is my workaround. Need to do this "properly".
class JsonPutCommand extends AbstractRestCommand {

    private final Object post;

    public JsonPutCommand(String baseUrl, String relativeUrl, Object post){
        super(baseUrl, relativeUrl, null);
        this.post = post;
    }

    public JsonPutCommand(String baseUrl, String relativeUrl, Object post, Object... urlParameters) {
        super(baseUrl, relativeUrl, urlParameters);
        this.post = post;
    }

    @Override
    public ClientResponse executeInternal(WebResource resource) {
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, post);
        return response;
    }

    @Override
    public <T> T execute(Class<T> clazz) throws RestCommandException {
        return super.execute(clazz, Status.OK.getStatusCode());
    }

}

class JsonDeleteCommand extends AbstractRestCommand {

	public JsonDeleteCommand(String baseUrl, String relativeUrl) {
		super(baseUrl, relativeUrl, null);
	}

	public JsonDeleteCommand(String baseUrl, String relativeUrl, Object... urlParameters) {
		super(baseUrl, relativeUrl, urlParameters);
	}

	@Override
	public ClientResponse executeInternal(WebResource resource) {
		ClientResponse response = resource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
		return response;
	}

	@Override
	public <T> T execute(Class<T> clazz) throws RestCommandException {
		return super.execute(clazz, Status.OK.getStatusCode());
	}

}