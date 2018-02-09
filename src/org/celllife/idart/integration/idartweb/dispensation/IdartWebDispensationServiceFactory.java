package org.celllife.idart.integration.idartweb.dispensation;

import org.celllife.idart.client.IdartClient;
import org.celllife.idart.client.IdartClientSingleton;
import org.celllife.idart.commonobjects.iDartProperties;

/**
 * User: Kevin W. Sewell
 * Date: 2013-04-26
 * Time: 13h16
 */
public final class IdartWebDispensationServiceFactory {

    public synchronized static IdartWebDispensationService getInstance() {
        IdartClient idartClient = null;
        if (iDartProperties.proxyBypass) {
        	idartClient = IdartClientSingleton.getInstance(
                    iDartProperties.idartWebUrl,
                    iDartProperties.idartWebSystemId,
                    iDartProperties.idartWebApplicationKey);
        } else {
        	idartClient = IdartClientSingleton.getInstance(
                    iDartProperties.idartWebUrl,
                    iDartProperties.idartWebSystemId,
                    iDartProperties.idartWebApplicationKey,
                    iDartProperties.proxyUrl,
                    iDartProperties.proxyPort,
                    iDartProperties.proxyUser,
                    iDartProperties.proxyPassword,
                    iDartProperties.proxyUserDomain);
        }
        return new IdartWebDispensationServiceImpl(idartClient);
    }
}
