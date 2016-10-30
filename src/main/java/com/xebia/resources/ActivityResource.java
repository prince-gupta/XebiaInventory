package com.xebia.resources;

import com.xebia.annotations.Secured;
import com.xebia.dto.ActionResult;
import com.xebia.dto.ActivityDTO;
import com.xebia.dto.UserDto;
import com.xebia.enums.EventEnum;
import com.xebia.services.IActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.*;
import java.util.Map;

/**
 * Created by Pgupta on 29-10-2016.
 */
@Component
@Path("/activities")
@Secured
public class ActivityResource {

    @Autowired
    IActivityService activityService;

    @GET
    @Path("/getOptionsForActivityPage")
    @Produces("application/json")
    public ActionResult getOptions(){
        ActionResult actionResult = new ActionResult();
        actionResult.setStatus(ActionResult.Status.SUCCESS);
        actionResult.addData("users", activityService.getITAndAdminUsers());
        actionResult.addData("actions", EventEnum.values());
        return actionResult;
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public ActionResult fetchActivities(@RequestBody ActivityDTO searchDto){
        ActionResult actionResult = new ActionResult();
        actionResult.setStatus(ActionResult.Status.SUCCESS);
        Map resultMap = activityService.fetchActivities(searchDto);
        actionResult.addData("result", resultMap.get("result"));
        actionResult.addData("count", resultMap.get("count"));
        return actionResult;
    }
}
