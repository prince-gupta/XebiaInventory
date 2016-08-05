package com.xebia.resources;

import com.xebia.Secured;
import com.xebia.dto.PrintDto;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.Path;

/**
 * Created by Pgupta on 27-07-2016.
 */
@Component
@Path("/print")
@Secured
public class PrintResource {

    public void printStatement(@RequestBody PrintDto printDto){

    }
}
