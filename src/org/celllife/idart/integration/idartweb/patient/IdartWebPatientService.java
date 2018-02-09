package org.celllife.idart.integration.idartweb.patient;

/**
 * iDARTweb service with operations related to patients
 */
public interface IdartWebPatientService {

	/**
	 * Gets a list of the current patients in iDARTweb and then updates the local database - creating new patients
	 * and merging existing patients where necessary.
	 * 
	 * If any error occurs, the exception is handled - the transaction is rolled back and an error is displayed
	 * to the user. The list of patients will not be updated.
	 * 
	 * @param identifier String
	 */
    void updatePatients(String identifier);

}
