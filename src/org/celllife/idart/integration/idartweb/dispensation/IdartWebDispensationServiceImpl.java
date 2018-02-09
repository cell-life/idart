package org.celllife.idart.integration.idartweb.dispensation;

import static org.celllife.idart.common.Quantity.newQuantity;
import static org.celllife.idart.commonobjects.iDartProperties.idartWebSystemId;

import java.util.Arrays;

import org.celllife.idart.client.IdartClient;
import org.celllife.idart.client.dispensation.DispensationBuilder;
import org.celllife.idart.client.dispensation.DispensedMedicationBuilder;
import org.celllife.idart.common.Quantity;
import org.celllife.idart.common.Systems;
import org.celllife.idart.common.UnitOfMeasureCode;
import org.celllife.idart.common.UnitsOfMeasure;
import org.celllife.idart.database.hibernate.Doctor;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Form;
import org.celllife.idart.database.hibernate.PackagedDrugs;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PrescribedDrugs;
import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.database.hibernate.Stock;
import org.celllife.idart.integration.idartweb.IdartWebException;

/**
 * Default implementation of IdartWebDispensationService
 */
final class IdartWebDispensationServiceImpl implements IdartWebDispensationService {

    private IdartClient idartClient;

    public IdartWebDispensationServiceImpl(IdartClient idartClient) {
        this.idartClient = idartClient;
    }

    @Override
    public void saveDispensation(Packages packages) throws IdartWebException {
    	
    	try {

	        Prescription prescription = packages.getPrescription();
	
	        // TODO Not right! Find a way to get hold of the pharmacist
	        Doctor doctor = prescription.getDoctor();
	
	        Patient patient = prescription.getPatient();
	
	        DispensationBuilder dispensationBuilder = newDispensationBuilder()
	                .setIdentifier(packages.getPackageId())
	                .setPatient(Systems.IDART_WEB.id, patient.getPatientId())
	                .setDispenser(Systems.IDART_WEB.id, doctor.getIdentifier())
	                .setHandedOver(packages.getPickupDate());
	
	        for (PackagedDrugs packagedDrugs : packages.getPackagedDrugs()) {
	
	            Stock stock = packagedDrugs.getStock();
	
	            Drug drug = stock.getDrug();
	            String medication = getMedicationIdentifier(drug.getId());
	
	            PrescribedDrugs prescribedDrugs = prescription.getPrescribedDrugs(drug);
	            String authorizingPrescribedMedication = getPrescribedMedicationIdentifier(prescribedDrugs.getId());
	
	            Form form = drug.getForm();
	            Quantity quantity = newQuantity((double) packagedDrugs.getAmount(), getUnitOfMeasureCode(form));
	
	            dispensationBuilder.addDispensedMedication(newDispensedMedicationBuilder()
	                    .setAuthorizingPrescribedMedication(authorizingPrescribedMedication)
	                    .setQuantity(quantity)
	                    .setMedication(medication)
	                    .setExpectedSupplyDuration(4, UnitsOfMeasure.wk.code)
	                    .setDosageQuantity(
	                            prescribedDrugs.getAmtPerTime(),
	                            getUnitOfMeasureCode(form)
	                    )
	                    .repeat(prescribedDrugs.getTimesPerDay())
	                    .every(1, UnitsOfMeasure.d.code)
	                    .finishDispensedMedication()
	            );
	        }
	
	        idartClient.saveDispensation(dispensationBuilder.finishDispensation());
    	} catch (Exception e) {
    		throw new IdartWebException("Error while communicating with iDARTweb - unable to save drug packages '"+packages.getPackageId()+"'.", e);
    	}
    }
    
    @Override
    public void deleteDispensation(Packages packages) throws IdartWebException {
    	try {
    		// convert the idart package object to the iDARTweb dispensation object (using a builder)
    		// Note: only the ID is really needed for the delete operation (from iDART standalone)
	        DispensationBuilder dispensationBuilder = newDispensationBuilder()
	                .setIdentifier(packages.getPackageId());
	        // delete!!!
	        idartClient.deleteDispensation(dispensationBuilder.finishDispensation());

        } catch (Exception e) {
            throw new IdartWebException("Error while communicating with iDARTweb - unable to delete dispensation '"+packages.getPackageId()+"'.", e);
        }
    }

    private DispensationBuilder newDispensationBuilder() {
        return new DispensationBuilder(idartWebSystemId);
    }

    private DispensedMedicationBuilder newDispensedMedicationBuilder() {
        return new DispensedMedicationBuilder(idartWebSystemId);
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
