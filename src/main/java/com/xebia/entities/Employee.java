package com.xebia.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.math.BigInteger;


/**
 * The persistent class for the employee database table.
 * 
 */
@Entity
@NamedQuery(name="Employee.findAll", query="SELECT e FROM Employee e")
public class Employee implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private BigInteger id;

	@Column(name="approvals_required")
	private String approvalsRequired;

	@Temporal(TemporalType.DATE)
	@Column(name="date_of_joining")
	private Date dateOfJoining;

	@Column(name="e_code")
	private String eCode;

	@Column(name="first_name")
	private String firstName;

	@Column(name="last_name")
	private String lastName;

    @Column(name="email")
    private String email;

    @Column(name="mobile")
    private String mobile;

	public Employee() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getApprovalsRequired() {
		return this.approvalsRequired;
	}

	public void setApprovalsRequired(String approvalsRequired) {
		this.approvalsRequired = approvalsRequired;
	}

	public Date getDateOfJoining() {
		return this.dateOfJoining;
	}

	public void setDateOfJoining(Date dateOfJoining) {
		this.dateOfJoining = dateOfJoining;
	}

	public String getECode() {
		return this.eCode;
	}

	public void setECode(String eCode) {
		this.eCode = eCode;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}