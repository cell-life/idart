package org.celllife.idart.integration.idartweb.medication;

import static org.celllife.idart.common.Label.label;
import static org.celllife.idart.commonobjects.iDartProperties.idartWebSystemId;

import java.util.Arrays;
import java.util.Set;

import org.celllife.idart.client.IdartClient;
import org.celllife.idart.client.part.Compound;
import org.celllife.idart.client.part.CompoundBuilder;
import org.celllife.idart.client.part.DrugBuilder;
import org.celllife.idart.client.product.BillOfMaterialsItemBuilder;
import org.celllife.idart.client.product.Medication;
import org.celllife.idart.client.product.MedicationBuilder;
import org.celllife.idart.common.Label;
import org.celllife.idart.common.PartClassificationType;
import org.celllife.idart.common.UnitOfMeasureCode;
import org.celllife.idart.common.UnitsOfMeasure;
import org.celllife.idart.database.hibernate.ChemicalCompound;
import org.celllife.idart.database.hibernate.ChemicalDrugStrength;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.integration.idartweb.IdartWebException;

/**
 * Default implementation of IdartWebMedicationService
 */
final class IdartWebMedicationServiceImpl implements IdartWebMedicationService {
	
	//private static final Logger LOGGER = LoggerFactory.getLogger(IdartWebMedicationServiceImpl.class);
	public static String DEFAULT_ATCCODE = "J05A";

    private IdartClient idartClient;

    public IdartWebMedicationServiceImpl(IdartClient idartClient) {
        this.idartClient = idartClient;
    }

    @Override
    public void saveMedication(Drug localMedication) throws IdartWebException {
    	
    	try {
	        DrugBuilder individualDrugBuilder = newDrug(idartWebSystemId)
	                .setIdentifier(getIndividualDrugIdentifier(localMedication.getId()))
	                .setForm(localMedication.getForm().getCode())
	                .setLabel(Label.valueOf(localMedication.getName()))
	                .setQuantity(
	                		localMedication.getPackSize(),
	                		getUnitOfMeasureCode(localMedication.getForm())
	                );

	        if (localMedication.getAtccode() != null && !localMedication.getAtccode().getCode().isEmpty()) {
	        	// set the atc code to the one the user specified (easy!)
	        	String atcCode = localMedication.getAtccode().getCode();
	        	individualDrugBuilder.addClassification(PartClassificationType.ATC, atcCode);
	        } else if (localMedication.getChemicalCompounds() != null && localMedication.getChemicalCompounds().size() == 1) {
	        	// if there is only 1 compound with 1 atc code, then we can use that
	        	ChemicalCompound compound = localMedication.getChemicalCompounds().iterator().next();
	        	Set<org.celllife.idart.database.hibernate.AtcCode> atcCodes = compound.getAtccodes();
	        	if (atcCodes != null && atcCodes.size() == 1) {
	        		org.celllife.idart.database.hibernate.AtcCode atcCode = atcCodes.iterator().next();
        			individualDrugBuilder.addClassification(PartClassificationType.ATC, atcCode.getCode());
	        	}
	        }
	
	        for (ChemicalDrugStrength chemicalDrugStrength : localMedication.getChemicalDrugStrengths()) {
	
	            Compound compound = newCompound(idartWebSystemId)
	                    .setIdentifier(getCompoundIdentifier(chemicalDrugStrength.getChemicalCompound().getId()))
	                    .setLabel(label(chemicalDrugStrength.getChemicalCompound().getName()))
	                    .finishCompound();
	
	            idartClient.savePart(compound);
	
	            individualDrugBuilder.addBillOfMaterialsItem(newBillOfMaterialsItem()
	                    .setQuantity(chemicalDrugStrength.getStrength(), UnitsOfMeasure.mg.code)
	                    .addPart(compound.getIdentifiers())
	                    .finishBillOfMaterialsItem()
	            );
	        }
	
	        org.celllife.idart.client.part.Drug individualDrug = individualDrugBuilder.finishDrug();
	        if (individualDrug.getClassifications().size() == 0) {
	        	// if no classification was set, then use the default one
	        	individualDrugBuilder.addClassification(PartClassificationType.ATC, DEFAULT_ATCCODE);
	        	individualDrug = individualDrugBuilder.finishDrug();
	        }
	        idartClient.savePart(individualDrug);
	
	        Medication medication = new MedicationBuilder(idartWebSystemId)
	                .setIdentifier(getMedicationIdentifier(localMedication.getId()))
	                .setName(localMedication.getName())
	                .addDrug(individualDrug.getIdentifiers())
	                .finishMedication();
	
	        idartClient.saveProduct(medication);

    	} catch (Exception e) {
    		throw new IdartWebException("Error while communicating with iDARTweb - unable to save drug '"+localMedication.getName()+"'.", e);
        }
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

    static String getPackageIdentifier(Integer id) {
        return String.format("P%07d", id);
    }

    static String getIndividualDrugIdentifier(Integer id) {
        return String.format("I%07d", id);
    }

    static String getCompoundIdentifier(Integer id) {
        return String.format("%08d", id);
    }

    static BillOfMaterialsItemBuilder newBillOfMaterialsItem() {
        return new BillOfMaterialsItemBuilder();
    }

    static DrugBuilder newDrug(String clinicIdentifier) {
        return new DrugBuilder(clinicIdentifier);
    }

    static CompoundBuilder newCompound(String clinicIdentifier) {
        return new CompoundBuilder(clinicIdentifier);
    }
}
