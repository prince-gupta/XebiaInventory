package com.xebia.services.impl;

import com.xebia.dao.EventMailDAO;
import com.xebia.dao.UserDAO;
import com.xebia.dao.UserRoleDAO;
import com.xebia.dto.ActivityDTO;
import com.xebia.dto.UserDto;
import com.xebia.entities.EventMail;
import com.xebia.entities.User;
import com.xebia.entities.UserRole;
import com.xebia.enums.UserRoleEnum;
import com.xebia.services.IActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            tempDto.setActionItem(eventMail.getType());
            tempDto.setEventDate(eventMail.getEventDate());
            activityDTOs.add(tempDto);
        }
        Map resultMap = new HashMap<>();
        resultMap.put("result",activityDTOs);
        resultMap.put("count", dbResultMap.get("count"));
        return resultMap;
    }
}
