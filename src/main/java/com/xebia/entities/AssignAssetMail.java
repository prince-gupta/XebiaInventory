package com.xebia.entities;

import com.xebia.enums.MailStatus;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;


/**
 * The persistent class for the assign_asset_mails database table.
 * 
 */
@Entity
@Table(name="assign_asset_mails")
@NamedQuery(name="AssignAssetMail.findAll", query="SELECT a FROM AssignAssetMail a")
public class AssignAssetMail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private BigInteger id;

	//uni-directional many-to-one association to Asset
	@ManyToOne
	@JoinColumn(name="asset")
	private Asset asset;

	//uni-directional many-to-one association to Employee
	@ManyToOne
	@JoinColumn(name="approver")
	private Employee approver;

	//uni-directional many-to-one association to Employee
	@ManyToOne
	@JoinColumn(name="employee")
	private Employee employee;

    @Column(name="status")
    private String status;

    @Transient
    private Date dateOfIssue;

    @Transient
    private Date dateTillValid;

    @Transient
    private Date dateofReturned;

    @Column(name="asset_status")
    private String assetStatus;

    @Column(name="updated_on")
    private Date updatedDate;

	public AssignAssetMail() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public Asset getAsset() {
		return this.asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public Employee getApprover() {
		return this.approver;
	}

	public void setApprover(Employee approver) {
		this.approver = approver;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDateOfIssue() {
        return dateOfIssue;
    }

    public void setDateOfIssue(Date dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    public Date getDateTillValid() {
        return dateTillValid;
    }

    public void setDateTillValid(Date dateTillValid) {
        this.dateTillValid = dateTillValid;
    }

    public String getAssetStatus() {
        return assetStatus;
    }

    public void setAssetStatus(String assetStatus) {
        this.assetStatus = assetStatus;
    }

    public Date getDateofReturned() {
        return dateofReturned;
    }

    public void setDateofReturned(Date dateofReturned) {
        this.dateofReturned = dateofReturned;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}