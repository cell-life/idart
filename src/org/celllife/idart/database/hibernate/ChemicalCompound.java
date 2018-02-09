package org.celllife.idart.database.hibernate;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 */
@Entity
public class ChemicalCompound {

	@Id
	@GeneratedValue
	private Integer id;
	
	private String acronym;

	private String name;

	@OneToMany(mappedBy = "chemicalCompound")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private Set<ChemicalDrugStrength> chemicalDrugStrengths;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name = "atccode_chemicalcompound", joinColumns = { @JoinColumn(name = "chemicalcompound_id") }, inverseJoinColumns = { @JoinColumn(name = "atccode_id") })
	@ForeignKey(inverseName="fk_atccode_chemicalcompound",name="fk_chemicalcompound_atccode")
	private Set<AtcCode> atccodes;

	public ChemicalCompound() {
	}

	/**
	 * Constructor for ChemicalCompound.
	 * @param name String
	 * @param acronym String
	 */
	public ChemicalCompound(String name, String acronym) {
		this.name = name;
		this.acronym = acronym;
	}

	/**
	 * Method getAcronym.
	 * @return String
	 */
	public String getAcronym() {
		return acronym == null ? name : acronym;
	}

	/**
	 * Method getName.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Method getChemicalDrugStrengths.
	 * @return Set<ChemicalDrugStrength>
	 */
	public Set<ChemicalDrugStrength> getChemicalDrugStrengths() {
		return chemicalDrugStrengths;
	}

	/**
	 * Method getId.
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method setAcronym.
	 * @param acronym String
	 */
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	/**
	 * Method setChemicalDrugStrengths.
	 * @param ChemicalDrugStrengths Set<ChemicalDrugStrength>
	 */
	public void setChemicalDrugStrengths(
			Set<ChemicalDrugStrength> ChemicalDrugStrengths) {
		this.chemicalDrugStrengths = ChemicalDrugStrengths;
	}

	/**
	 * Method setName.
	 * @param name String
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Method setId.
	 * @param id int
	 */
	public void setId(int id) {
		this.id = id;
	}

	public void setAtccodes(Set<AtcCode> atccodes) {
		this.atccodes = atccodes;
	}

	public Set<AtcCode> getAtccodes() {
		if (atccodes == null){
			atccodes = new HashSet<AtcCode>();
		}
		return atccodes;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChemicalCompound that = (ChemicalCompound) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ChemicalCompound [acronym=").append(acronym)
				.append(", name=").append(name).append("]");
		return builder.toString();
	}
	
	
}
