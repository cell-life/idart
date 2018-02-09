package org.celllife.idart.integration.idartweb.medication;

import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.integration.idartweb.IdartWebException;

/**
 * Service with operations related to Medication (Drugs)
 */
public interface IdartWebMedicationService {

	/**
	 * Submits the new drug to iDARTWeb
	 * @param drug Drug new medication
	 * @throws IdartWebException if any error occurs while trying to save drug
	 */
    void saveMedication(Drug drug) throws IdartWebException;

}
