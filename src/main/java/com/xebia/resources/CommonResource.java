package com.xebia.resources;

import com.xebia.annotations.Secured;
import com.xebia.dao.ExcelMappingDAO;
import com.xebia.entities.ExcelMapping;
import com.xebia.services.monitor.IMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import javax.ws.rs.*;
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
}
