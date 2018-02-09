/*
 * iDART: The Intelligent Dispensing of Antiretroviral Treatment
 * Copyright (C) 2006 Cell-Life 
 * 
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License version 2 as published by 
 * the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT  
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or  
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License version  
 * 2 for more details. 
 * 
 * You should have received a copy of the GNU General Public License version 2 
 * along with this program; if not, write to the Free Software Foundation, 
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 * 
 */

package org.celllife.idart.database.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Defines a Drug Manufacturer known by the iDART system
 */
@Entity
public class StockManufacturer {

	@Id
	@GeneratedValue
	private Integer id;

	private String name;
	
	public StockManufacturer() {
		super();
	}

	/**
	 * Retrieve the primary key of the Entity.
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set the primary key of the Entity (usually done by Hibernate)
	 * @param id int
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the name of the Drug Manufacturer. This value is used in the Stock entity
	 * @return String name e.g. Cipla
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of a known Drug Manufacturer.
	 * @param name String name e.g. Cipla
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StockManufacturer other = (StockManufacturer) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StockManufacturer [id=" + id + ", name=" + name + "]";
	}

}
