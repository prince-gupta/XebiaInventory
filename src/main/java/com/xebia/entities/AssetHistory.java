package com.xebia.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.math.BigInteger;


/**
 * The persistent class for the asset_history database table.
 * 
 */
@Entity
@Table(name="asset_history")
@NamedQuery(name="AssetHistory.findAll", query="SELECT a FROM AssetHistory a")
public class AssetHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private BigInteger id;

	@Temporal(TemporalType.DATE)
	@Column(name="issue_date")
	private Date issueDate;

	private String status;

	@Temporal(TemporalType.DATE)
	@Column(name="valid_till")
	private Date validTill;

	//uni-directional many-to-one association to Employee
	@ManyToOne
	@JoinColumn(name="approved_by")
	private Employee employee1;

	//uni-directional many-to-one association to Asset
	@ManyToOne
	@JoinColumn(name="asset_id")
	private Asset asset;

	//uni-directional many-to-one association to Employee
	@ManyToOne
	@JoinColumn(name="issued_to")
	private Employee employee2;

    @Temporal(TemporalType.DATE)
    @Column(name="returned_date")
    private Date returnedDate;

	public AssetHistory() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public Date getIssueDate() {
		return this.issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getValidTill() {
		return this.validTill;
	}

	public void setValidTill(Date validTill) {
		this.validTill = validTill;
	}

	public Employee getEmployee1() {
		return this.employee1;
	}

	public void setEmployee1(Employee employee1) {
		this.employee1 = employee1;
	}

	public Asset getAsset() {
		return this.asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public Employee getEmployee2() {
		return this.employee2;
	}

	public void setEmployee2(Employee employee2) {
		this.employee2 = employee2;
	}

    public Date getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(Date returnedDate) {
        this.returnedDate = returnedDate;
    }
}