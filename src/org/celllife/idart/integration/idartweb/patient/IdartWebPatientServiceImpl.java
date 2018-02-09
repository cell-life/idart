package org.celllife.idart.integration.idartweb.patient;

import static model.manager.PatientManager.findIdentifierTypeBySystem;
import static model.manager.PatientManager.savePatient;

import java.util.List;

import model.manager.AdministrationManager;
import model.manager.PatientManager;

import org.celllife.idart.client.IdartClient;
import org.celllife.idart.client.partyrole.Patient;
import org.celllife.idart.client.person.MobileTelephoneNumber;
import org.celllife.idart.client.person.PartyContactMechanism;
import org.celllife.idart.common.Identifier;
import org.celllife.idart.common.Systems;
import org.celllife.idart.database.hibernate.IdentifierType;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.misc.MessageUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of IdartWebPatientService
 */
final class IdartWebPatientServiceImpl implements IdartWebPatientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdartWebPatientServiceImpl.class);

    private Session session;

    private IdartClient idartClient;

    public IdartWebPatientServiceImpl(IdartClient idartClient) {
        this.idartClient = idartClient;
        this.session = HibernateUtil.getNewSession();
    }

    @Override
    public void updatePatients(String identifier) {

        Transaction transaction = session.beginTransaction();

        try {

            doUpdatePatients(identifier);

            transaction.commit();

        } catch (Exception e) {
            transaction.rollback();
            LOGGER.error("Error while communicating with iDARTweb - unable to update patient with identifier '"+identifier+"'", e);
            MessageUtil.showError(e, "iDART Error",	MessageUtil.getIDARTWebCrashMessage(e));
        }
    }

    private void doUpdatePatients(String patientIdentifierValue) {

        List<Patient> newPatients = idartClient.getPatients(patientIdentifierValue);

        for (Patient newPatient : newPatients) {

            Identifier identifier = newPatient.identifiers.iterator().next();

            if (identifier == null) {
                continue;
            }

            String value = identifier.getValue();
            String system = identifier.getSystem().getValue();
            if (value == null || value.isEmpty()) {
                continue;
            }

            org.celllife.idart.database.hibernate.Patient existingPatient
                    = PatientManager.findPatientByIdentifierValueAndType(session, value, system);

            if (existingPatient == null) {
                existingPatient = createNewPatient();
            }
            update(newPatient, existingPatient);

            savePatient(session, existingPatient);
        }
    }

    private org.celllife.idart.database.hibernate.Patient createNewPatient() {

        org.celllife.idart.database.hibernate.Patient patient = new org.celllife.idart.database.hibernate.Patient();
        patient.setModified('T');
        patient.setClinic(AdministrationManager.getMainClinic(session));

        return patient;
    }

    private void update(Patient newPatient, org.celllife.idart.database.hibernate.Patient existingPatient) {

        existingPatient.setFirstNames(newPatient.person.firstName);
        existingPatient.setLastname(newPatient.person.lastName);
        existingPatient.setDateOfBirth(newPatient.person.birthDate);
        for (PartyContactMechanism contactMechanism : newPatient.person.contactMechanisms) {
            if (contactMechanism.contactMechanism instanceof MobileTelephoneNumber) {
                existingPatient.setCellphone(contactMechanism.contactMechanism.toString());
            }
        }
        existingPatient.setSex(newPatient.person.gender.name().charAt(0));

        for (Identifier identifier : newPatient.identifiers) {
            IdentifierType identifierType = findIdentifierTypeBySystem(session, identifier.getSystem().getValue());
            existingPatient.addOrUpdatePatientIdentifier(identifier.getValue(), identifierType);
        }

        IdentifierType idartPatientIdentifierType = findIdentifierTypeBySystem(session, Systems.IDART_WEB.toString());
        existingPatient.updateDefaultPatientIdentifier(idartPatientIdentifierType);
        
        String patientIdentifier = newPatient.getIdentifierBySystem(Systems.IDART_WEB.id);
        existingPatient.setPatientId(patientIdentifier);
    }
}
