package com.xebia.resources;

import com.xebia.annotations.Secured;
import com.xebia.dto.ActionResult;
import com.xebia.dto.PageRoleDTO;
import com.xebia.dto.UserDto;
import com.xebia.dto.UserRoleDTO;
import com.xebia.entities.PageRole;
import com.xebia.entities.User;
import com.xebia.exception.ApplicationException;
import com.xebia.exception.ParsingException;
import com.xebia.services.IPageRoleService;
import com.xebia.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pgupta on 07-08-2016.
 */

@Component
@Path("/user")
public class UserResource {

    @Autowired
    IUserService userService;

    @Context
    HttpServletRequest httpServletRequest;

    @Autowired
    IPageRoleService pageRoleService;


    @Secured
    @CrossOrigin(origins = "http://localhost:3000")
    @GET
    @Path("fetchAllUsers")
    @Produces("application/json")
    public ActionResult fetchAllUsers() {
        ActionResult result = new ActionResult();
        result.setStatus(ActionResult.Status.SUCCESS);
        result.addData("list", userService.fetchAllUsers());
        return result;
    }

    @POST
    @Consumes("application/json")
    @Path("/createUser")
    @Produces("application/json")
    @Secured
    public ActionResult createUser(@RequestBody UserDto user) {
        ActionResult result = new ActionResult();
        try {
            User new_user = userService.createUser(user);
            Map data = new HashMap<>();
            data.put("user", new_user);
            result.setData(data);
            result.setStatus(ActionResult.Status.SUCCESS);
        } catch (ApplicationException e) {
            Map data = new HashMap<>();
            String[] messagePair = e.getMessage().split(":");
            data.put("eMessage", messagePair[0]);
            data.put("eAddInfo", messagePair[1]);

            result.setData(data);
            result.setStatus(ActionResult.Status.FAILURE);
        }
        return result;
    }

    @POST
    @Consumes("application/json")
    @Path("/resetPassword")
    @Produces("application/json")
    @Secured
    public ActionResult resetPassword(@RequestBody User user) {
        ActionResult result = new ActionResult();
        try {
            userService.resetPassword(user.getUsername());
            result.setStatus(ActionResult.Status.SUCCESS);
        } catch (ApplicationException e) {
            Map data = new HashMap<>();
            data.put("eMessage", "User Name not found !");
            result.setData(data);
            result.setStatus(ActionResult.Status.FAILURE);
        }
        return result;
    }

    @GET
    @Path("/getRoles")
    @Produces("application/json")
    @Secured
    public ActionResult getRoles() {
        ActionResult result = new ActionResult();
        result.setStatus(ActionResult.Status.SUCCESS);
        result.addData("list", userService.getRoles());
        return result;
    }

    @POST
    @Path("/getUserRoles")
    @Consumes("application/json")
    @Produces("application/json")
    @Secured
    public ActionResult getUserRoles(@RequestBody BigInteger id) {
        ActionResult result = new ActionResult();
        result.setStatus(ActionResult.Status.SUCCESS);
        result.addData("list", userService.getUserRoles(id));
        return result;
    }

    @POST
    @Path("/updateUserRoles")
    @Produces("application/json")
    @Consumes("application/json")
    @Secured
    public ActionResult updateUserRoles(@RequestBody UserRoleDTO userRoleDTO) {
        ActionResult result = new ActionResult();
        userService.updateUserRoles(new BigInteger(userRoleDTO.getId()), userRoleDTO.getRoleIds());
        result.setStatus(ActionResult.Status.SUCCESS);
        return result;
    }

    @GET
    @Path("/populatePageRoles")
    @Produces("application/json")
    @Secured
    public ActionResult populatePageRoles() {
        ActionResult result = new ActionResult();
        try {
            pageRoleService.populatePageRoles();
            result.setStatus(ActionResult.Status.SUCCESS);
        } catch (ApplicationException e) {
            result.setStatus(ActionResult.Status.FAILURE);
            Map error = new HashMap<>();
            error.put("eMessage", e.getMessage());
            result.setError(error);
        } catch (ParsingException e) {
            result.setStatus(ActionResult.Status.FAILURE);
            Map error = new HashMap<>();
            error.put("eMessage", e.getMessage());
            result.setError(error);
        }
        return result;
    }

    @GET
    @Path("/getPageRolesCount")
    @Produces("application/json")
    public long getPageRolesCount() {
       return pageRoleService.getPageRolesCount();
    }

    @GET
    @Path("/getPageRoles")
    @Produces("application/json")
    @Secured
    public ActionResult getPageRoles(@QueryParam("offset") int offset, @QueryParam("limit") int limit) {
        ActionResult result = new ActionResult();
        result.addData("list", pageRoleService.getPageRoles(offset, limit));
        result.setStatus(ActionResult.Status.SUCCESS);
        return result;
    }
}
