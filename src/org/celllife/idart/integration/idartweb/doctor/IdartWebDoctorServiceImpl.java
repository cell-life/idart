package org.celllife.idart.integration.idartweb.doctor;

import org.celllife.idart.client.IdartClient;
import org.celllife.idart.client.partyrole.Practitioner;
import org.celllife.idart.common.Systems;
import org.celllife.idart.database.hibernate.Doctor;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.misc.MessageUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static model.manager.AdministrationManager.findDoctorByIdentifier;
import static model.manager.AdministrationManager.saveDoctor;

/**
 * Default implementation of IdartWebDoctorService
 */
final class IdartWebDoctorServiceImpl implements IdartWebDoctorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdartWebDoctorServiceImpl.class);

    private Session session;

    private IdartClient idartClient;

    public IdartWebDoctorServiceImpl(IdartClient idartClient) {
        this.idartClient = idartClient;
        this.session = HibernateUtil.getNewSession();
    }

    @Override
    public void updateDoctors() {

        Transaction transaction = session.beginTransaction();

        try {

            doUpdateDoctors();

            transaction.commit();

        } catch (Exception e) {
            transaction.rollback();
            LOGGER.error("Error while communicating with iDARTweb - unable to update doctors.", e);
            MessageUtil.showError(e, "iDART Error",	MessageUtil.getIDARTWebCrashMessage());
        }
    }

    private void doUpdateDoctors() {

        List<Practitioner> practitioners = idartClient.getPractitioners();

        for (Practitioner practitioner : practitioners) {

            String doctorIdentifier = practitioner.getIdentifierBySystem(Systems.IDART_WEB.id);

            if (doctorIdentifier == null || doctorIdentifier.isEmpty()) {
                continue;
            }

            Doctor doctor = findDoctorByIdentifier(session, doctorIdentifier);

            if (doctor == null) {
                doctor = new Doctor();
                doctor.setModified('T');
            }

            updateExistingDoctor(practitioner, doctor);
            saveDoctor(session, doctor);
        }
    }

    private void updateExistingDoctor(Practitioner newDoctor, Doctor existingDoctor) {
        existingDoctor.setActive(true);
        existingDoctor.setFirstname(newDoctor.person.firstName);
        existingDoctor.setLastname(newDoctor.person.lastName);
        existingDoctor.setIdentifier(newDoctor.getIdentifierBySystem(Systems.IDART_WEB.id));
    }
}
