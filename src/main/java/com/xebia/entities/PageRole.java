package com.xebia.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;


/**
 * The persistent class for the page_roles database table.
 * 
 */
@Entity
@Table(name="page_roles")
@NamedQuery(name="PageRole.findAll", query="SELECT p FROM PageRole p")
public class PageRole implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private BigInteger id;

	private String name;

	private String url;

	//bi-directional many-to-many association to UserRole
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name="page_role_mappings"
		, joinColumns={
			@JoinColumn(name="page_role")
			}
		, inverseJoinColumns={
			@JoinColumn(name="user_role")
			}
		)
	private List<UserRole> userRoles;

	public PageRole() {
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<UserRole> getUserRoles() {
		return this.userRoles;
	}

	public void setUserRoles(List<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

}