package com.xebia.services;

import com.xebia.dto.ActivityDTO;
import com.xebia.dto.UserDto;

import java.util.List;
import java.util.Map;

/**
 * Created by Pgupta on 29-10-2016.
 */
public interface IActivityService {

    public List<UserDto> getITAndAdminUsers();

    public Map fetchActivities(ActivityDTO activityDTO);
}
