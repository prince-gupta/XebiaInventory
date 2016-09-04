package com.xebia.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


/**
 * The persistent class for the page_role_mappings database table.
 * 
 */
@Entity
@Table(name="page_role_mappings")
@NamedQuery(name="PageRoleMapping.findAll", query="SELECT p FROM PageRoleMapping p")
public class PageRoleMapping implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private BigInteger id;

	//uni-directional many-to-one association to PageRole
	@ManyToOne
	@JoinColumn(name="page_role")
	private PageRole pageRoleBean;

	//uni-directional many-to-one association to UserRole
	@ManyToOne
	@JoinColumn(name="user_role")
	private UserRole userRoleBean;

	public PageRoleMapping() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public PageRole getPageRoleBean() {
		return this.pageRoleBean;
	}

	public void setPageRoleBean(PageRole pageRoleBean) {
		this.pageRoleBean = pageRoleBean;
	}

	public UserRole getUserRoleBean() {
		return this.userRoleBean;
	}

	public void setUserRoleBean(UserRole userRoleBean) {
		this.userRoleBean = userRoleBean;
	}

}