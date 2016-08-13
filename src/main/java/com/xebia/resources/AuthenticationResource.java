package com.xebia.resources;

import com.xebia.Secured;
import com.xebia.dto.ActionResult;
import com.xebia.dto.AuthenticationResponse;
import com.xebia.entities.User;
import com.xebia.exception.AuthenticationException;
import com.xebia.services.IAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

/**
 * Created by Pgupta on 02-08-2016.
 */
@Path("/logon")
public class AuthenticationResource {

    @Autowired
    IAuthenticationService authenticationService;

    @Context
    HttpServletRequest httpServletRequest;

    @GET
    @Produces("application/text")
    @Path("/dummy")
    public String dummy() {
        return  "OK";
    }

    @POST
    @Consumes("application/json")
    @Path("/generateToken")
    public AuthenticationResponse generateToken(User user) {
        String ip = httpServletRequest.getRemoteAddr();
        return  authenticationService.generateToken(user.getUsername(),user.getPassword(), ip);
    }

    @POST
    @Consumes("application/json")
    @Path("/createUser")
    @Produces("application/json")
    public User createUser(User user) {
        return authenticationService.createUser(user);
    }

    @POST
    @Consumes("application/json")
    @Path("/changePassword")
    @Produces("application/json")
    public ActionResult changePassword(User user){
        String ip = httpServletRequest.getRemoteAddr();
        ActionResult result = new ActionResult();
        try {
            authenticationService.authenticateToken(user.getToken(), user.getUsername(), ip);
            authenticationService.changePassword(user);
            result.setStatus(ActionResult.Status.SUCCESS);
        }
        catch (AuthenticationException e){
            result.setStatus(ActionResult.Status.FAILURE);
        }
        return result;
    }

    @POST
    @Consumes("application/json")
    @Path("/logout")
    @Produces("application/json")
    public ActionResult logout(User user){
        ActionResult result = new ActionResult();
        try {
            authenticationService.logout(user);
            result.setStatus(ActionResult.Status.SUCCESS);
        }
        catch (AuthenticationException e){
            result.setStatus(ActionResult.Status.FAILURE);
        }
        return result;
    }
}
