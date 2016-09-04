package com.xebia.services;

import com.xebia.dto.PageRoleDTO;
import java.util.List;

/**
 * Created by Pgupta on 01-09-2016.
 */
public interface IPageRoleService {

    public void populatePageRoles();

    public List<PageRoleDTO> getPageRoles();
}
