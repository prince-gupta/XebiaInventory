package com.xebia.services.impl;

import com.xebia.dao.*;
import com.xebia.dto.ActivityDTO;
import com.xebia.dto.UserDto;
import com.xebia.entities.*;
import com.xebia.enums.EventType;
import com.xebia.enums.UserRoleEnum;
import com.xebia.services.IActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Pgupta on 29-10-2016.
 */
@Service
public class ActivityServiceImpl implements IActivityService {

    @Autowired
    UserRoleDAO userRoleDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    EventMailDAO eventMailDAO;

    @Autowired
    AssetDAO assetDAO;

    @Autowired
    EmployeeDAO employeeDAO;

    @Autowired
    AssetTypeDAO assetTypeDAO;

    @Autowired
    ManufacturerDAO manufacturerDAO;

    Map<EventType, RefrenceResolver> commandMap = new HashMap<>();

    @PostConstruct
    public void prepareCommands(){

        commandMap.put(EventType.ASSET, (refId, eventType) -> {
            Asset asset = assetDAO.getById(refId);
            return asset.getName() +"-"+ asset.getSerialNumber();

        });

        commandMap.put(EventType.EMP, (refId, eventType) -> {
            Employee employee = employeeDAO.getById(refId);
            return employee.getFullName() + "("+employee.getECode()+")";

        });

        commandMap.put(EventType.ASSET_TYPE, (refId, eventType) -> {
            AssetType assetType = assetTypeDAO.getById(refId);
            return assetType.getType();

        });

        commandMap.put(EventType.ASSET_MANUFACTURER, (refId, eventType) -> {
            AssetManufacturer assetManufacturer = manufacturerDAO.getById(refId);
            return assetManufacturer.getName();
        });

        commandMap.put(EventType.USER, (refId, eventType) -> {
            User user = userDAO.getById(refId);
            return user.getUsername();
        });

        commandMap.put(EventType.ROLE, (refId, eventType) -> {
            UserRole userRole = userRoleDAO.getById(refId);
            return userRole.getRoleName();
        });
    }

    @Override
    public List<UserDto> getITAndAdminUsers() {
        List<UserRole> roles = userRoleDAO.getByRoleName(UserRoleEnum.IT.getDbValue(), UserRoleEnum.ADMIN.getDbValue());
        Set<User> userSet  = new HashSet<>();
        for(UserRole role : roles){
            userSet.addAll(role.getUsers());
        }
        List<UserDto> userDtos = new ArrayList<>();
        for(User user : userSet){
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setUserName(user.getUsername());
            userDto.setFullName(user.getEmployee().getFullName());
            userDto.setEcode(user.getEmployee().getECode());
            userDtos.add(userDto);
        }
        return userDtos;
    }

    public Map fetchActivities(ActivityDTO activityDTO){
        if(activityDTO.getAction() != null && activityDTO.getAction().equals("Select Action")){
            activityDTO.setAction(null);
        }
        Map dbResultMap = eventMailDAO.getByActivityObject(activityDTO);
        List<ActivityDTO> activityDTOs = new ArrayList<>();
        List<EventMail> eventMailList = (List)(dbResultMap.get("result"));
        for(EventMail eventMail : eventMailList){
            User user = userDAO.getById(eventMail.getUser());
            ActivityDTO tempDto = new ActivityDTO();
            tempDto.setUserName(user.getUsername());
            tempDto.setFullName(user.getEmployee().getFullName());
            tempDto.setAction(eventMail.getEvent());
            tempDto.setActionItemType(eventMail.getType());
            EventType eventType = EventType.getEventType(eventMail.getType());
            tempDto.setActionItem(commandMap.get(eventType).resolve(eventMail.getRefId(),eventType));
            tempDto.setEventDate(eventMail.getEventDate());
            activityDTOs.add(tempDto);
        }
        Map resultMap = new HashMap<>();
        resultMap.put("result",activityDTOs);
        resultMap.put("count", dbResultMap.get("count"));
        return resultMap;
    }

    private interface RefrenceResolver {
        public String resolve(BigInteger refId, EventType eventType);
    }
}
