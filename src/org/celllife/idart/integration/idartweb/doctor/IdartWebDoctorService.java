package org.celllife.idart.integration.idartweb.doctor;

/**
 * iDARTweb service with operations related to Doctors
 */
public interface IdartWebDoctorService {
	
	/**
	 * Gets a list of the current doctors in iDARTweb and then updates the local database - creating new doctors
	 * and merging existing doctors where necessary.
	 * 
	 * If any error occurs, the exception is handled - the transaction is rolled back and an error is displayed
	 * to the user. The list of doctors will not be updated.
	 */
    void updateDoctors();
}
