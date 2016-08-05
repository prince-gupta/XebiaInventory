package com.xebia.resources;

import com.xebia.dto.AuthenticationResponse;
import com.xebia.entities.User;
import com.xebia.services.IAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
        return authenticationService.createUser(user.getUsername());
    }
}
