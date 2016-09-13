package com.xebia.services.impl;

import com.xebia.common.ResouceHandler;
import com.xebia.common.XMLParser;
import com.xebia.config.mapping.pagerole.Config;
import com.xebia.config.mapping.pagerole.Configurations;
import com.xebia.dao.PageRoleDao;
import com.xebia.dao.UserRoleDAO;
import com.xebia.dto.PageRoleDTO;
import com.xebia.entities.PageRole;
import com.xebia.entities.UserRole;
import com.xebia.exception.ApplicationException;
import com.xebia.exception.ParsingException;
import com.xebia.services.IPageRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pgupta on 01-09-2016.
 */
@Service
public class PageRoleServiceImpl implements IPageRoleService {

    @Autowired
    ResouceHandler resouceHandler;

    @Autowired
    PageRoleDao pageRoleDao;

    @Autowired
    UserRoleDAO userRoleDAO;


    @Override
    public void populatePageRoles() throws ApplicationException, ParsingException {
        Configurations configurations;
        try {
            configurations = XMLParser.unMarshall(resouceHandler.getFileStream("config/pageRoleConfigurations.xml"), Configurations.class);
            Map<String, PageRole> pageRoleMap = new HashMap<>();
            for (Config config : configurations.getConfig()) {
                String displayName = config.getDisplayName().trim();
                if (pageRoleMap.get(displayName) != null && !(pageRoleMap.get(displayName).getUrl().equals(config.getUrl().trim()))) {
                    throw new ParsingException("Inconsistency in mapping : No url mapping can have same displayName = " + config.getDisplayName().trim());
                }
                PageRole pageRole = pageRoleDao.getByName(displayName);
                if (pageRole == null) {
                    pageRole = new PageRole();
                    pageRole.setName(displayName);
                    pageRole.setUrl(config.getUrl().trim());
                    pageRoleDao.create(pageRole);
                }
                pageRoleMap.put(displayName, pageRole);

                String[] roleNames = config.getRoleName().trim().split(",");
                for(String roleName : roleNames) {
                    UserRole dbRole = userRoleDAO.getByRoleName(roleName);
                    if (dbRole == null)
                        throw new ParsingException("Undefined Role : Role Name configured in " + displayName + " does not present in DB.");
                    PageRole dbPageRole = pageRoleDao.getByName(displayName);
                    List<UserRole> savedUserRoles = dbPageRole.getUserRoles();
                    boolean alreadySaved = false;
                    for (UserRole userRole : savedUserRoles) {
                        if (userRole.getRoleName().equals(dbRole.getRoleName())) {
                            alreadySaved = true;
                            break;
                        }
                    }
                    if (!alreadySaved) {
                        dbPageRole.getUserRoles().add(dbRole);
                        pageRoleDao.update(dbPageRole);
                    }
                }
            }
        } catch (IOException e) {
            throw new ApplicationException("Got Exception while trying to convert pageRoleConfiguration.xml to Object.");
        }
    }

    private Map<String, List<String>> parseXMLForUserRoles(Configurations configurations) {
        Map<String, List<String>> map = new HashMap<>();
        for (Config config : configurations.getConfig()) {
            List<String> userRoles;
            if (map.get(config.getDisplayName().trim()) != null) {
                userRoles = map.get(config.getDisplayName().trim());
            } else {
                userRoles = new ArrayList<>();
            }
            String userRole = (config.getRoleName().trim());
            userRoles.add(userRole);
            map.put(config.getDisplayName().trim(), userRoles);
        }
        return map;
    }

    public List<PageRoleDTO> getPageRoles(int offset, int limit) {
        List<PageRole> dbPageRoles = pageRoleDao.getAll(offset, limit);
        List<PageRoleDTO> pageRoles = new ArrayList<>();
        for (PageRole pageRole : dbPageRoles) {
            PageRoleDTO pageRoleDTO = new PageRoleDTO();
            pageRoleDTO.setId(pageRole.getId());
            pageRoleDTO.setName(pageRole.getName());
            pageRoleDTO.setUrl(pageRole.getUrl());
            List<String> userRoles = new ArrayList<>();
            for (UserRole userRole : pageRole.getUserRoles()) {
                userRoles.add(userRole.getRoleName());
            }
            pageRoleDTO.setUserRole(userRoles);
            pageRoles.add(pageRoleDTO);
        }
        return pageRoles;
    }

    public long getPageRolesCount(){
        return pageRoleDao.getCount();
    }
}
