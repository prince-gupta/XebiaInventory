package com.xebia.resources;

import com.xebia.annotations.Secured;
import com.xebia.dao.EmployeeDAO;
import com.xebia.dao.UserDAO;
import com.xebia.dto.ActionResult;
import com.xebia.dto.EmployeeSearchDTO;
import com.xebia.entities.Employee;
import com.xebia.entities.User;
import com.xebia.exception.ApplicationException;
import com.xebia.services.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.Map;

@Component
@Path("/employee")
@Secured
public class EmployeeResource {

    @Autowired
    EmployeeDAO employeeDAO;

    @Autowired
    IEmployeeService employeeService;

    @Context
    HttpServletRequest httpServletRequest;

    @Autowired
    UserDAO userDAO;

    @GET
    @Path("/getTotalEmployeeCount")
    @Produces("application/json")
    public long getCount() {
        return employeeDAO.getCount();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GET
    @Path("fetchAll")
    @Produces("application/json")
    public ActionResult getAllEmployee(@QueryParam("offset") int offset, @QueryParam("limit") int limit) {
        ActionResult result = new ActionResult();
        result.setStatus(ActionResult.Status.SUCCESS);
        result.addData("list", employeeDAO.getAll(offset, limit));
        return result;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("fetch")
    @Produces("application/json")
    @Consumes("application/json")
    public ActionResult getEmployee(@RequestBody EmployeeSearchDTO employee) {
        ActionResult result = new ActionResult();
        Map resultMap = employeeDAO.getByEmployeeObject(employee);
        result.addData("result", resultMap.get("result"));
        result.addData("count", resultMap.get("count"));
        return result;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GET
    @Path("fetchEmployeeDetails")
    @Produces("application/json")
    public Employee getEmployee() {
        String userName = httpServletRequest.getHeader("Username");
        User user = userDAO.getUserByUName(userName);
        return employeeDAO.getById(user.getEmployee().getId());
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("create")
    @Produces("application/json")
    @Consumes("application/json")
    public ActionResult create(@RequestBody Employee employee) {
        ActionResult result = new ActionResult();
        try {
            employeeService.create(employee, httpServletRequest.getHeader("Username"));
            result.setStatus(ActionResult.Status.SUCCESS);
        } catch (ApplicationException e) {
            result.getError().put("errorMsg", "Employee Code is not available. Assigned to other employee.");
            result.setStatus(ActionResult.Status.FAILURE);
        }
        return result;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("update")
    @Produces("application/json")
    @Consumes("application/json")
    public ActionResult update(@RequestBody Employee employee) {
        ActionResult result = new ActionResult();
        try {
            employeeService.update(employee, httpServletRequest.getHeader("Username"));
            result.setStatus(ActionResult.Status.SUCCESS);
        } catch (ApplicationException e) {
            result.setStatus(ActionResult.Status.FAILURE);
            result.getError().put("errorMsg", "Employee Code is not available. Assigned to other employee.");
            return result;
        }
        return result;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GET
    @Path("getApprovers")
    @Produces("application/json")
    public List<Employee> approvers() {
        return employeeDAO.getApprovers();
    }

    @POST
    @Path("delete")
    @Consumes("application/json")
    @Produces("application/json")
    public ActionResult delete(@RequestBody String eCode) {
        ActionResult result = new ActionResult();
        try {
            employeeService.delete(eCode, httpServletRequest.getHeader("Username"));
            result.setStatus(ActionResult.Status.SUCCESS);
        } catch (ApplicationException a) {
            result.setStatus(ActionResult.Status.FAILURE);
            result.addData("eMessage", a.getMessage());
        }
        return result;
    }

    @POST
    @Path("/updateEmployee")
    @Produces("application/json")
    @Consumes("application/json")
    public ActionResult updateEmployee(@RequestBody Employee employee) {
        ActionResult result = new ActionResult();
        try {
            User user = userDAO.getUserByUName(httpServletRequest.getHeader("Username"));
            Employee dbEmployee = user.getEmployee();
            dbEmployee.setMobile(employee.getMobile());
            employeeDAO.update(dbEmployee);
            result.setStatus(ActionResult.Status.SUCCESS);
        } catch (Exception e) {
            result.setStatus(ActionResult.Status.FAILURE);
        }
        return result;

    }

    @POST
    @Path("/isECodeAvailable")
    @Produces("application/json")
    @Consumes("application/json")
    public ActionResult isECodeAvailable(@RequestBody Employee employee){
        ActionResult result = new ActionResult();
        if(employeeDAO.countEmployee(employee.getECode()) > 0){
            result.setStatus(ActionResult.Status.FAILURE);
        }
        return result;
    }
}
