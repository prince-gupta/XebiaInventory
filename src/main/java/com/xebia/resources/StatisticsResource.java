package com.xebia.resources;

import com.xebia.annotations.Secured;
import com.xebia.dto.AssetStatisticsDTO;
import com.xebia.services.IStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Pgupta on 11-10-2016.
 */
@Component
@Secured
@Path("/statistics")
public class StatisticsResource {

    @Autowired
    IStatisticsService statisticsService;

    @Path("assetStats")
    @Produces("application/json")
    @GET
    public List<AssetStatisticsDTO> getAssetStats(){
        return statisticsService.getAssetStatistics();
    }
}
