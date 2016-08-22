package com.xebia.resources;

import com.xebia.Secured;
import com.xebia.dao.ExcelMappingDAO;
import com.xebia.entities.ExcelMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import javax.ws.rs.*;
import java.util.List;

/**
 * Created by Pgupta on 20-08-2016.
 */
@Component
@Secured
@Path("/common")
public class CommonResource {

    @Autowired
    ExcelMappingDAO excelMappingDAO;

    @GET
    @Path("/getExcelMappings")
    @Produces("application/json")
    public List<ExcelMapping> getExcelMappings() {
        return excelMappingDAO.getAll();
    }

    @POST
    @Path("/updateExcelMappings")
    @Produces("application/json")
    @Consumes("application/json")
    public List<ExcelMapping> updateExcelMappings(@RequestBody List<ExcelMapping> mappings) {
        mappings.forEach(excelMappingDAO::update);
        return excelMappingDAO.getAll();
    }
}
