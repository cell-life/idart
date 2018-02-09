package org.celllife.idart.integration.idartweb.doctor;

import org.celllife.idart.client.IdartClient;
import org.celllife.idart.client.IdartClientSingleton;
import org.celllife.idart.commonobjects.iDartProperties;

/**
 * User: Kevin W. Sewell
 * Date: 2013-04-30
 * Time: 07h46
 */
public final class IdartWebDoctorServiceFactory {

    public static IdartWebDoctorService getInstance() {
        IdartClient idartClient = IdartClientSingleton.getInstance(
                iDartProperties.idartWebUrl,
                iDartProperties.idartWebSystemId,
                iDartProperties.idartWebApplicationKey
        );
        return new IdartWebDoctorServiceImpl(idartClient);
    }
}
