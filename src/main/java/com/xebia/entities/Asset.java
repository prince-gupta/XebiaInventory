package com.xebia.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.math.BigInteger;


/**
 * The persistent class for the assets database table.
 * 
 */
@Entity
@Table(name="assets")
@NamedQuery(name="Asset.findAll", query="SELECT a FROM Asset a")
public class Asset implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="asset_id")
	private BigInteger assetId;

	@Temporal(TemporalType.DATE)
	@Column(name="date_of_purchase")
	private Date dateOfPurchase;

	private String name;

	@Column(name="serial_number")
	private String serialNumber;

	//uni-directional many-to-one association to Employee
	@ManyToOne
	@JoinColumn(name="issued_to")
	private Employee employee;

	//uni-directional many-to-one association to AssetManufacturer
	@ManyToOne
	@JoinColumn(name="manufacturer")
	private AssetManufacturer assetManufacturer;

	//uni-directional many-to-one association to AssetType
	@ManyToOne
	@JoinColumn(name="type")
	private AssetType assetType;

    @ManyToOne
    @JoinColumn(name="configuration")
    private HardwareConfiguration hardwareConfiguration;

	public Asset() {
	}

	public BigInteger getAssetId() {
		return this.assetId;
	}

	public void setAssetId(BigInteger assetId) {
		this.assetId = assetId;
	}

	public Date getDateOfPurchase() {
		return this.dateOfPurchase;
	}

	public void setDateOfPurchase(Date dateOfPurchase) {
		this.dateOfPurchase = dateOfPurchase;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSerialNumber() {
		return this.serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public AssetManufacturer getAssetManufacturer() {
		return this.assetManufacturer;
	}

	public void setAssetManufacturer(AssetManufacturer assetManufacturer) {
		this.assetManufacturer = assetManufacturer;
	}

	public AssetType getAssetType() {
		return this.assetType;
	}

	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;
	}

    public HardwareConfiguration getHardwareConfiguration() {
        return hardwareConfiguration;
    }

    public void setHardwareConfiguration(HardwareConfiguration hardwareConfiguration) {
        this.hardwareConfiguration = hardwareConfiguration;
    }
}