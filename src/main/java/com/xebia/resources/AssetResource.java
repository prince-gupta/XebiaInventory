package com.xebia.resources;

import com.xebia.Secured;
import com.xebia.dao.AssetDAO;
import com.xebia.dto.AssetDto;
import com.xebia.dto.AssetTypeDto;
import com.xebia.entities.*;
import com.xebia.services.IAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.*;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by Pgupta on 23-07-2016.
 */
@Component
@Secured
@Path("/asset")
public class AssetResource {

    @Autowired
    IAssetService assetService;
    
    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("createAssetType")
    @Produces("application/text")
    @Consumes("application/json")
    public String createAssetType(@RequestBody AssetType assetType){
        return assetService.createAssetType(assetType);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GET
    @Path("fetchAllAssetType")
    @Produces("application/json")
    public List<AssetTypeDto> getAllAssetType(){
        return assetService.getAllAssetType();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("deleteAssetType")
    @Produces("application/text")
    public String deleteAssetType(@RequestBody String id){
        return assetService.deleteAssetType(id);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("updateAssetType")
    @Produces("application/text")
    public String updateAssetType(@RequestBody AssetType assetType){
        return assetService.updateAssetType(assetType);
    }


    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("createAsset")
    @Produces("application/text")
    @Consumes("application/json")
    public String createAsset(@RequestBody AssetDto asset){
        return assetService.createAsset(asset);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GET
    @Path("fetchAllAsset")
    @Produces("application/json")
    public List<Asset> getAllAsset(){
        return assetService.getAllAsset();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("fetchAssetByType")
    @Produces("application/json")
    public List<Asset> getAsset(@RequestBody BigInteger typeId){
        return assetService.getAssetByType(typeId);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("fetchAvailableAssetByType")
    @Produces("application/json")
    public List<Asset> fetchAvailableAssetByType(@RequestBody BigInteger typeId){
        return assetService.getAvailableAssetByType(typeId);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("deleteAsset")
    @Produces("application/text")
    public String deleteAsset(@RequestBody String id){
       return assetService.deleteAsset(id);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("assignAsset")
    @Produces("application/text")
    public String assignAsset(@RequestBody AssetDto assetDto){
        return assetService.assignAsset(assetDto);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("unAssignAsset")
    @Produces("application/text")
    public String unAssignAsset(@RequestBody AssetDto assetDto){
        return assetService.unAssignAsset(assetDto);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("updateAsset")
    @Produces("application/text")
    public String updateAsset(@RequestBody Asset asset){
        return assetService.updateAsset(asset);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("fetchEmployeeAsset")
    @Produces("application/json")
    public List<AssetDto> fetchAssetByEmployee(@RequestBody BigInteger employeeID){
        return assetService.getEmployeeAsset(employeeID);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("createAssetManufacturer")
    @Produces("application/text")
    @Consumes("application/json")
    public String createAssetManufacturer(@RequestBody AssetManufacturer assetManufacturer){
        return assetService.createAssetManufacturer(assetManufacturer);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GET
    @Path("fetchAllAssetManufacturer")
    @Produces("application/json")
    public List<AssetManufacturer> getAllAssetManufacturer(){
        return assetService.getAllAssetManufacturer();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("deleteAssetManufacturer")
    @Produces("application/text")
    public String deleteAssetManufacturer(@RequestBody String id){
        return assetService.deleteAssetManufacturer(id);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("updateAssetManufacturer")
    @Produces("application/text")
    public String updateAssetManufacturer(@RequestBody AssetManufacturer assetManufacturer){
        return assetService.updateAssetManufacturer(assetManufacturer);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GET
    @Path("fetchAllConfiguration")
    @Produces("application/json")
    public List<HardwareConfiguration> fetchHarwareConfigurations(){
        return assetService.getHardwareConfigurations();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("createHardwareConfigurations")
    @Produces("application/text")
    @Consumes("application/json")
    public String createHarwareConfiguration(@RequestBody HardwareConfiguration hardwareConfiguration){
        return assetService.createHardwareConfig(hardwareConfiguration);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("updateHardwareConfigurations")
    @Produces("application/text")
    @Consumes("application/json")
    public String updateHarwareConfiguration(@RequestBody HardwareConfiguration hardwareConfiguration){
        return assetService.updateHardwareConfig(hardwareConfiguration);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @POST
    @Path("getAssetsHistory")
    @Produces("application/json")
    public List<AssetHistory> getAssetHistory(@RequestBody BigInteger employeeId){
        return assetService.getAssetsHistory(employeeId);
    }
}
