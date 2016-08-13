package com.xebia.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;


/**
 * The persistent class for the user database table.
 *
 */
@Entity
@NamedQuery(name="User.findAll", query="SELECT u FROM User u")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private BigInteger id;

    private String active;

    private String password;

    private BigInteger role;

    private String token;

    private String username;

    private String changePassword;

    @OneToOne
    @JoinColumn(name="employee")
    private Employee employee;

    //bi-directional many-to-many association to UserRole
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="user_role_mapping"
            , joinColumns={
            @JoinColumn(name="user")
    }
            , inverseJoinColumns={
            @JoinColumn(name="role")
    }
    )
    private List<UserRole> userRoles;

    public User() {
    }

    public BigInteger getId() {
        return this.id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getActive() {
        return this.active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public Employee getEmployee() {
        return this.employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BigInteger getRole() {
        return this.role;
    }

    public void setRole(BigInteger role) {
        this.role = role;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<UserRole> getUserRoles() {
        return this.userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public String getChangePassword() {
        return changePassword;
    }

    public void setChangePassword(String changePassword) {
        this.changePassword = changePassword;
    }
}