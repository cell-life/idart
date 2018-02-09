package org.celllife.idart.integration.idartweb.prescription;

import static org.celllife.idart.commonobjects.iDartProperties.idartWebSystemId;

import java.util.Arrays;

import org.celllife.idart.client.IdartClient;
import org.celllife.idart.client.prescription.PrescribedMedicationBuilder;
import org.celllife.idart.client.prescription.PrescriptionBuilder;
import org.celllife.idart.common.Systems;
import org.celllife.idart.common.UnitOfMeasureCode;
import org.celllife.idart.common.UnitsOfMeasure;
import org.celllife.idart.database.hibernate.PrescribedDrugs;
import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.integration.idartweb.IdartWebException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of IdartWebPrescriptionService
 */
final class IdartWebPrescriptionServiceImpl implements IdartWebPrescriptionService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IdartWebPrescriptionServiceImpl.class);

    private IdartClient idartClient;

    public IdartWebPrescriptionServiceImpl(IdartClient idartClient) {
        this.idartClient = idartClient;
    }

    @Override
    public void savePrescription(Prescription prescription) throws IdartWebException {
    	
    	try {

	        PrescriptionBuilder prescriptionBuilder = new PrescriptionBuilder(idartWebSystemId)
	                .setIdentifier(prescription.getPrescriptionId())
	                .setPatient(Systems.IDART_WEB.id, prescription.getPatient().getPatientId())
	                .setPrescriber(Systems.IDART_WEB.id, prescription.getDoctor().getIdentifier())
	                .setDateWritten(prescription.getDate());
	
	        for (PrescribedDrugs prescribedDrugs : prescription.getPrescribedDrugs()) {
	        	LOGGER.info("prescribedDrugs.getDrug().getId() 1="+prescribedDrugs.getDrug().getId()+" 2="+getMedicationIdentifier(prescribedDrugs.getDrug().getId()));
	            prescriptionBuilder
	                    .addPrescribedMedication(newPrescribedMedication()
	                            .setId(getPrescribedMedicationIdentifier(prescribedDrugs.getId()))
	                            .setMedication(getMedicationIdentifier(prescribedDrugs.getDrug().getId()))
	                            .setExpectedSupplyDuration(prescription.getDuration(), UnitsOfMeasure.wk.code)
	                            .setReasonForPrescribing(prescription.getReasonForUpdate())
	                            .setValid(prescription.getDate(), prescription.getEndDate())
	                            .setDosageQuantity(
	                                    prescribedDrugs.getAmtPerTime(),
	                                    getUnitOfMeasureCode(prescribedDrugs.getDrug().getForm())
	                            )
	                            .repeat(prescribedDrugs.getTimesPerDay())
	                            .every(1, UnitsOfMeasure.d.code)
	                            .finishPrescribedMedication()
	                    );
	        }
	
	        idartClient.savePrescription(prescriptionBuilder.finishPrescription());
	        
        } catch (Exception e) {
            throw new IdartWebException("Error while communicating with iDARTweb - unable to save prescription '"+prescription.getPrescriptionId()+"'.", e);
        }

    }
    
    @Override
    public void deletePrescription(Prescription prescription) throws IdartWebException {
    	try {
    		// convert the idart prescription object to the iDARTweb prescription object (using a builder)
	        PrescriptionBuilder prescriptionBuilder = new PrescriptionBuilder(idartWebSystemId)
	        .setIdentifier(prescription.getPrescriptionId())
	        .setPatient(Systems.IDART_WEB.id, prescription.getPatient().getPatientId())
	        .setPrescriber(Systems.IDART_WEB.id, prescription.getDoctor().getIdentifier())
	        .setDateWritten(prescription.getDate());
        
	        // delete!!!
	        idartClient.deletePrescription(prescriptionBuilder.finishPrescription());

        } catch (Exception e) {
            throw new IdartWebException("Error while communicating with iDARTweb - unable to delete prescription '"+prescription.getPrescriptionId()+"'.", e);
        }
    }

    private PrescribedMedicationBuilder newPrescribedMedication() {
        return new PrescribedMedicationBuilder(idartWebSystemId);
    }

    static UnitOfMeasureCode getUnitOfMeasureCode(org.celllife.idart.database.hibernate.Form form) {

        if (Arrays.asList("SYRUP").contains(form.getCode())) {
            return UnitsOfMeasure.mL.code;
        }

        return UnitsOfMeasure.each.code;
    }


    static String getMedicationIdentifier(Integer id) {
        return String.format("%08d", id);
    }

    static String getPrescribedMedicationIdentifier(int id) {
        return String.format("%08d", id);
    }
}
