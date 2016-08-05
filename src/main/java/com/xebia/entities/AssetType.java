package com.xebia.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


/**
 * The persistent class for the asset_type database table.
 * 
 */
@Entity
@Table(name="asset_type")
@NamedQuery(name="AssetType.findAll", query="SELECT a FROM AssetType a")
public class AssetType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private BigInteger id;

	private String type;

	public AssetType() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}