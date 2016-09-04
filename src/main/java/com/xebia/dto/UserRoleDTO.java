package com.xebia.dto;

import java.util.List;

/**
 * Created by Pgupta on 31-08-2016.
 */
public class UserRoleDTO {

    private String id;

    private List<String> roleIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }
}
