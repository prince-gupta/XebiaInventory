package com.xebia.resources;

import com.xebia.Secured;
import com.xebia.dao.EmployeeDAO;
import com.xebia.dto.ActionResult;
import com.xebia.entities.Employee;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.*;
import java.sql.Date;
import java.util.List;

@Component
@Path("/employee")
@Secured
public class EmployeeResource {

    @Autowired
    EmployeeDAO employeeDAO;

    @CrossOrigin(origins = "http://localhost:3000")
    @GET
    @Path("fetchAll")
    @Produces("application/json")
    public List<Employee> getAllEmployee() {
        return employeeDAO.getAll();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("fetch")
    @Produces("application/json")
    @Consumes("application/json")
    public List<Employee> getEmployee(@RequestBody Employee employee) {
        return employeeDAO.getByEmployeeObject(employee);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("create")
    @Produces("application/json")
    @Consumes("application/json")
    public ActionResult create(@RequestBody Employee employee) {
        ActionResult result = new ActionResult();
        if (employeeDAO.countEmployee(employee.getECode()) > 0) {
            result.setStatus(ActionResult.Status.FAILURE);
            result.getError().put("errorMsg","Employee Code is not available. Assigned to other employee.");
            return result;
        } else {
            if(StringUtils.equals("NA",employee.getApprovalsRequired()))
                employee.setApprovalsRequired("Y");
            employeeDAO.create(employee);
        }
        result.setStatus(ActionResult.Status.SUCCESS);
        return result;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("update")
    @Produces("application/json")
    @Consumes("application/json")
    public ActionResult update(@RequestBody Employee employee) {
        ActionResult result = new ActionResult();
        if (employeeDAO.countEmployee(employee.getECode()) > 0) {
            result.setStatus(ActionResult.Status.FAILURE);
            result.getError().put("errorMsg","Employee Code is not available. Assigned to other employee.");
            return result;
        } else {
            if(StringUtils.equals("NA",employee.getApprovalsRequired()))
                employee.setApprovalsRequired("Y");
            Employee dbEmployee = employeeDAO.getById(employee.getId());
            if(employee.getApprovalsRequired() != null){
                dbEmployee.setApprovalsRequired(employee.getApprovalsRequired());
            }
            if(employee.getECode() != null)
                dbEmployee.setECode(employee.getECode());
            if(employee.getEmail() != null)
                dbEmployee.setEmail(employee.getEmail());
            if(employee.getFirstName() != null)
                dbEmployee.setFirstName(employee.getFirstName());
            if(employee.getLastName() != null)
                dbEmployee.setLastName(employee.getLastName());
            if(employee.getMobile() != null)
                dbEmployee.setMobile(employee.getMobile());
            employeeDAO.update(dbEmployee);
        }
        result.setStatus(ActionResult.Status.SUCCESS);
        return result;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GET
    @Path("getApprovers")
    @Produces("application/json")
    public List<Employee> approvers() {
        return employeeDAO.getApprovers();
    }


}
