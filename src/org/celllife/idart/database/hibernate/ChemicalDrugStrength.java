package org.celllife.idart.database.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 */
@Entity
public class ChemicalDrugStrength {

	@ManyToOne
	@JoinColumn(name = "chemicalCompound")
	private ChemicalCompound chemicalCompound;

	@Id
	@GeneratedValue
	private Integer id;

	private int strength;

	@ManyToOne
	@JoinColumn(name = "drug")
	private Drug drug;

	public ChemicalDrugStrength() {
	}

	/**
	 * Constructor for ChemicalDrugStrength.
	 * 
	 * @param chemicalCompound
	 *            ChemicalCompound
	 * @param strength
	 *            int
	 * @param drug
	 *            Drug
	 */
	public ChemicalDrugStrength(ChemicalCompound chemicalCompound,
			int strength, Drug drug) {
		super();
		this.chemicalCompound = chemicalCompound;
		this.strength = strength;
		this.drug = drug;
	}

	/**
	 * Method getChemicalCompound.
	 * 
	 * @return ChemicalCompound
	 */
	public ChemicalCompound getChemicalCompound() {
		return chemicalCompound;
	}

	/**
	 * Method getId.
	 * 
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method getStrength.
	 * 
	 * @return int
	 */
	public int getStrength() {
		return strength;
	}

	/**
	 * Method getDrug.
	 * 
	 * @return Drug
	 */
	public Drug getDrug() {
		return drug;
	}

	/**
	 * Method setId.
	 * 
	 * @param id
	 *            int
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Method setStrength.
	 * 
	 * @param strength
	 *            int
	 */
	public void setStrength(int strength) {
		this.strength = strength;
	}

	/**
	 * Method setChemicalCompound.
	 * 
	 * @param chemicalCompound
	 *            ChemicalCompound
	 */
	public void setChemicalCompound(ChemicalCompound chemicalCompound) {
		this.chemicalCompound = chemicalCompound;
	}

	/**
	 * Method setDrug.
	 * 
	 * @param drug
	 *            Drug
	 */
	public void setDrug(Drug drug) {
		this.drug = drug;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChemicalDrugStrength that = (ChemicalDrugStrength) o;

        if (strength != that.strength) return false;
        if (chemicalCompound != null ? !chemicalCompound.equals(that.chemicalCompound) : that.chemicalCompound != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = chemicalCompound != null ? chemicalCompound.hashCode() : 0;
        result = 31 * result + strength;
        return result;
    }
}
