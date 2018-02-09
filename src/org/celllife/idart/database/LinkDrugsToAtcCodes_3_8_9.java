package org.celllife.idart.database;

import java.util.*;

import liquibase.change.custom.CustomSqlChange;
import liquibase.change.custom.CustomSqlRollback;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.SetupException;
import liquibase.exception.UnsupportedChangeException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.UpdateStatement;

import org.celllife.idart.database.hibernate.AtcCode;
import org.celllife.idart.database.hibernate.ChemicalCompound;
import org.celllife.idart.database.hibernate.ChemicalDrugStrength;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

public class LinkDrugsToAtcCodes_3_8_9 implements CustomSqlChange, CustomSqlRollback {

	@Override
	public String getConfirmationMessage() {
		return "Drugs mapped to ATC Codes";
	}

	@Override
	public void setFileOpener(ResourceAccessor fileOpener) {
	}

	@Override
	public void setUp() throws SetupException {
	}

	@Override
	public ValidationErrors validate(Database arg0) {
		return null;
	}
	
	@Override
	public liquibase.statement.SqlStatement[] generateStatements(Database arg0)
			throws CustomChangeException {

		Session session = HibernateUtil.getNewSession();
		
		List<SqlStatement> statements = new ArrayList<SqlStatement>();

        String drugsSql =
                "SELECT d.id, " +
                        "cds.chemicalcompound " +
                        "FROM drug d " +
                        "JOIN chemicaldrugstrength cds " +
                        "ON d.id = cds.drug";

        String atcCodesSql =
                "SELECT atc.id, atccc.chemicalcompound_id " +
                        "FROM atccode atc " +
                        "JOIN atccode_chemicalcompound atccc " +
                        "ON atc.id = atccc.atccode_id";

        Map<Integer, Set<Integer>> drugsMap = new HashMap<Integer, Set<Integer>>();

        SQLQuery drugsQuery = session.createSQLQuery(drugsSql);
        List<Object[]> drugs = drugsQuery.list();
        for (Object[] drug : drugs) {
            if (drugsMap.get(drug[0]) == null) {
                drugsMap.put((Integer) drug[0], new HashSet<Integer>());
            }
            drugsMap.get(drug[0]).add((Integer) drug[1]);
        }

        Map<Integer, Set<Integer>> atcCodesMap = new HashMap<Integer, Set<Integer>>();
        SQLQuery atcCodesQuery = session.createSQLQuery(atcCodesSql);
        List<Object[]> atcCodes = atcCodesQuery.list();
        for (Object[] atcCode : atcCodes) {
            if (atcCodesMap.get(atcCode[0]) == null) {
                atcCodesMap.put((Integer) atcCode[0], new HashSet<Integer>());
            }
            atcCodesMap.get(atcCode[0]).add((Integer) atcCode[1]);
        }

        for (Integer drugId : drugsMap.keySet()) {
            Set<Integer> thisChemicalCompoundIds = drugsMap.get(drugId);
            for (Integer atcCodeId : atcCodesMap.keySet()) {
                Set<Integer> thatChemicalCompoundIds = atcCodesMap.get(atcCodeId);
                if (thisChemicalCompoundIds.equals(thatChemicalCompoundIds)) {
                    statements.add(new UpdateStatement(null, "drug")
                            .addNewColumnValue("atccode_id", atcCodeId)
                            .setWhereClause("id = " + drugId));
                }
            }
        }

		return statements.toArray(new SqlStatement[statements.size()]);
	}

	@Override
	public SqlStatement[] generateRollbackStatements(Database arg0)
			throws CustomChangeException, UnsupportedChangeException,
			RollbackImpossibleException {
		return new SqlStatement[] {
				new UpdateStatement(null, "drug").addNewColumnValue("atccode_id", null)
		};
	}

}
