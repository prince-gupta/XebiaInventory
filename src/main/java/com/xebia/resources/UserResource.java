package com.xebia.resources;

import com.xebia.Secured;
import com.xebia.dao.UserDAO;
import com.xebia.dto.ActionResult;
import com.xebia.dto.UserDto;
import com.xebia.entities.User;
import com.xebia.exception.ApplicationException;
import com.xebia.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.*;
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


    @Secured
    @CrossOrigin(origins = "http://localhost:3000")
    @GET
    @Path("fetchAllUsers")
    @Produces("application/json")
    public List<UserDto> fetchAllUsers(){
        return userService.fetchAllUsers();
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
        }
        catch (ApplicationException e){
            Map data = new HashMap<>();
            String[] messagePair = e.getMessage().split(":");
            data.put("eMessage",messagePair[0]);
            data.put("eAddInfo",messagePair[1]);

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
        }
        catch (ApplicationException e){
            Map data = new HashMap<>();
            data.put("eMessage","User Name not found !");
            result.setData(data);
            result.setStatus(ActionResult.Status.FAILURE);
        }
        return result;
    }
}
