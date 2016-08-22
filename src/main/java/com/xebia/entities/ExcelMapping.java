package com.xebia.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


/**
 * The persistent class for the excel_mapping database table.
 * 
 */
@Entity
@Table(name="excel_mapping")
@NamedQuery(name="ExcelMapping.findAll", query="SELECT e FROM ExcelMapping e")
public class ExcelMapping implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private BigInteger id;

	@Column(name="app_column_name")
	private String appColumnName;

	private String datatype;

	@Column(name="excel_column_name")
	private String excelColumnName;

	public ExcelMapping() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getAppColumnName() {
		return this.appColumnName;
	}

	public void setAppColumnName(String appColumnName) {
		this.appColumnName = appColumnName;
	}

	public String getDatatype() {
		return this.datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getExcelColumnName() {
		return this.excelColumnName;
	}

	public void setExcelColumnName(String excelColumnName) {
		this.excelColumnName = excelColumnName;
	}

}