package com.xebia.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.math.BigInteger;


/**
 * The persistent class for the asset_approvals database table.
 * 
 */
@Entity
@Table(name="asset_approvals")
@NamedQuery(name="AssetApproval.findAll", query="SELECT a FROM AssetApproval a")
public class AssetApproval implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private BigInteger id;

    @ManyToOne
    @JoinColumn(name="asset_type")
    private AssetType assetType;

	@Temporal(TemporalType.DATE)
	@Column(name="date_till_valid")
	private Date dateTillValid;

    @Temporal(TemporalType.DATE)
    @Column(name="submitted_date")
    private Date submittedDate;

	private String remarks;

    private String remarks2;

	//uni-directional many-to-one association to Employee
	@ManyToOne
	@JoinColumn(name="raised_by")
	private Employee employee;

	//uni-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="approved_by")
	private User user;

    private String status;

	public AssetApproval() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public AssetType getAssetType() {
		return this.assetType;
	}

	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;
	}

	public Date getDateTillValid() {
		return this.dateTillValid;
	}

	public void setDateTillValid(Date dateTillValid) {
		this.dateTillValid = dateTillValid;
	}

    public Date getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks2() {
        return remarks2;
    }

    public void setRemarks2(String remarks2) {
        this.remarks2 = remarks2;
    }
}