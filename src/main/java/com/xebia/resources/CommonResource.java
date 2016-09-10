package com.xebia.resources;

import com.xebia.annotations.Secured;
import com.xebia.dao.ExcelMappingDAO;
import com.xebia.dao.UserDAO;
import com.xebia.dao.UserRoleDAO;
import com.xebia.entities.ExcelMapping;
import com.xebia.entities.User;
import com.xebia.entities.UserRole;
import com.xebia.services.monitor.IMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Pgupta on 20-08-2016.
 */
@Component
@Path("/common")
public class CommonResource {

    @Autowired
    ExcelMappingDAO excelMappingDAO;

    @Autowired
    IMonitorService monitorService;

    @Autowired
    UserRoleDAO userRoleDAO;

    @Autowired
    UserDAO userDAO;

    @Context
    HttpServletRequest httpServletRequest;

    @GET
    @Secured
    @Path("/getExcelMappings")
    @Produces("application/json")
    public List<ExcelMapping> getExcelMappings() {
        return excelMappingDAO.getAll();
    }

    @POST
    @Secured
    @Path("/updateExcelMappings")
    @Produces("application/json")
    @Consumes("application/json")
    public List<ExcelMapping> updateExcelMappings(@RequestBody List<ExcelMapping> mappings) {
        mappings.forEach(excelMappingDAO::update);
        return excelMappingDAO.getAll();
    }

    @GET
    @Path("/osInfo")
    @Produces("application/json")
    public Map getOsInfo(){
        return monitorService.osInfo();
    }

    @GET
    @Path("/getUserRoles")
    @Produces("application/json")
    public List<UserRole> getAllUserRoles(){
        String userName = httpServletRequest.getHeader("Username");
        User user = userDAO.getUserByUName(userName);
        return user.getUserRoles();
    }

    @POST
    @Path("/fileUpload")
    public void updateLogo(MultipartFile multipartFile) throws IOException {
        int size = httpServletRequest.getInputStream().available();
        byte[] bytes = new byte[size];
        httpServletRequest.getInputStream().read(bytes);
        FileOutputStream fos = new FileOutputStream("E:/temp.pdf");
        fos.write(bytes);
        fos.close();
    }
}
