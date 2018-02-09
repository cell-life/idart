package org.celllife.idart.integration.idartweb.prescription;

import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.integration.idartweb.IdartWebException;

/**
 * iDARTweb service with prescription related operations
 */
public interface IdartWebPrescriptionService {

	/**
	 * Sends the prescription to iDARTweb
	 * @param prescription Prescription to save
	 * @throws IdartWebException if any error occurs while saving the prescription
	 */
    void savePrescription(Prescription prescription) throws IdartWebException;

	/**
	 * Requests that a prescription is deleted from iDARTweb.
	 * @param prescription Prescription to delete
	 * @throws IdartWebException if any error occurs while deleting the prescription
	 */
    void deletePrescription(Prescription prescription) throws IdartWebException;
}
