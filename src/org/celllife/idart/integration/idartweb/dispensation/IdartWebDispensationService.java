package org.celllife.idart.integration.idartweb.dispensation;

import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.integration.idartweb.IdartWebException;

/**
 * iDARTweb service with operations related to dispensing medication (drugs) 
 */
public interface IdartWebDispensationService {

	/**
	 * Sends the dispensed drug package to iDARTweb
	 * @param packages Packages
	 * @throws IdartWebException
	 */
    void saveDispensation(Packages packages) throws IdartWebException;

	/**
	 * Requests that iDARTweb deletes packages
	 * @param packages Packages containg
	 * @throws IdartWebException
	 */
    void deleteDispensation(Packages packages) throws IdartWebException;
}
