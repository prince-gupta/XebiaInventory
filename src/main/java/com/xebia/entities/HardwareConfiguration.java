package com.xebia.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


/**
 * The persistent class for the hardware_configuration database table.
 * 
 */
@Entity
@Table(name="hardware_configuration")
@NamedQuery(name="HardwareConfiguration.findAll", query="SELECT h FROM HardwareConfiguration h")
public class HardwareConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private BigInteger id;

	private String cpu;

	private String hdd;

	private String name;

	private String ram;

	public HardwareConfiguration() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getCpu() {
		return this.cpu;
	}

	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

	public String getHdd() {
		return this.hdd;
	}

	public void setHdd(String hdd) {
		this.hdd = hdd;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRam() {
		return this.ram;
	}

	public void setRam(String ram) {
		this.ram = ram;
	}

}