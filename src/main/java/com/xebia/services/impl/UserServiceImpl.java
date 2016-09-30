package com.xebia.services.impl;

import com.xebia.common.Utility;
import com.xebia.dao.EmployeeDAO;
import com.xebia.dao.UserDAO;
import com.xebia.dao.UserRoleDAO;
import com.xebia.dto.EmployeeSearchDTO;
import com.xebia.dto.UserDto;
import com.xebia.entities.Employee;
import com.xebia.entities.User;
import com.xebia.entities.UserRole;
import com.xebia.enums.UserRoleEnum;
import com.xebia.exception.ApplicationException;
import com.xebia.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pgupta on 07-08-2016.
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    EmployeeDAO employeeDAO;

    @Autowired
    UserRoleDAO userRoleDAO;

    @Override
    public List<UserDto> fetchAllUsers() {
        List<UserDto> userDtos = new ArrayList<>();
        List<User> users = userDAO.getAll();
        for (User user : users) {
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setFullName(user.getEmployee().getFirstName() + " " + user.getEmployee().getLastName());
            userDto.setActive(user.getActive());
            userDto.setEcode(user.getEmployee().getECode());
            userDto.setUserName(user.getUsername());
            List<UserRole> userRoles = user.getUserRoles();
            StringBuffer rolesBuffer = new StringBuffer();
            for (int index = 0; index < userRoles.size(); index++) {
                rolesBuffer.append(userRoles.get(index).getRoleName());
                if (index + 1 != userRoles.size())
                    rolesBuffer.append(", ");
            }
            userDto.setRoles(rolesBuffer.toString());
            userDtos.add(userDto);
        }
        return userDtos;
    }

    @Override
    public User createUser(UserDto userDto) throws ApplicationException {

        if (checkIfUserNameAlreadyPresent(userDto.getUserName()))
            throw new ApplicationException("UN_Error:NA");

        EmployeeSearchDTO employee = new EmployeeSearchDTO();
        employee.setECode(userDto.getEcode());
        employee.setApprovalsRequired("NA");
        List<Employee> dbEmployeeList = (List)employeeDAO.getByEmployeeObject(employee).get("result");
        if (dbEmployeeList.size() == 0)
            throw new ApplicationException("EC_Error:NA");

        User userAlreadyPresent = userDAO.getUserByEmployee(dbEmployeeList.get(0));
        if (userAlreadyPresent != null)
            throw new ApplicationException("EC_PRESENT_ERROR:" + userAlreadyPresent.getUsername());

        String enCodedPass = Utility.encode("Xebia123");
        User user = new User();
        user.setPassword(enCodedPass);
        user.setActive("Y");
        user.setUsername(userDto.getUserName());
        user.setEmployee(dbEmployeeList.get(0));
        user.setChangePassword("Y");
        userDAO.create(user);
        user.setPassword("Xebia123");
        return user;
    }

    private boolean checkIfUserNameAlreadyPresent(String uName) {
        return userDAO.getUserByUName(uName) != null;
    }

    public void resetPassword(String username) throws ApplicationException {
        User user = userDAO.getUserByUName(username);
        if (user == null) {
            throw new ApplicationException("UserNotFound");
        }
        String enCodedPass = Utility.encode("Xebia123");
        user.setPassword(enCodedPass);
        user.setToken(null);
        user.setChangePassword("Y");
        userDAO.update(user);
    }

    public List<UserRole> getRoles() {
        List dbRoles = userRoleDAO.getRoles();
        List<UserRole> userRoles = new ArrayList<>();
        for (int index = 0; index < dbRoles.size(); index++) {
            Object[] roleArray = (Object[]) dbRoles.get(index);
            UserRole userRole = new UserRole();
            userRole.setId((BigInteger) roleArray[0]);
            userRole.setRoleName(UserRoleEnum.resolveByDBValue((String) roleArray[1]).getDisplayValue());
            userRoles.add(userRole);
        }
        return userRoles;
    }

    public List<UserRole> getUserRoles(BigInteger id) {
        User user = userDAO.getById(id);
        List<UserRole> dbRoles = user.getUserRoles();
        List<UserRole> userRoles = new ArrayList<>();
        for (int index = 0; index < dbRoles.size(); index++) {
            UserRole roleArray = dbRoles.get(index);
            UserRole userRole = new UserRole();
            userRole.setId(roleArray.getId());
            userRole.setRoleName(UserRoleEnum.resolveByDBValue(roleArray.getRoleName()).getDisplayValue());
            userRoles.add(userRole);
        }
        return userRoles;
    }

    public void updateUserRoles(BigInteger id, List<String> userRoles) {
        User user = userDAO.getById(id);
        if (userRoles.size() == 0) {
            user.setUserRoles(null);
        } else {
            List<UserRole> dbRoles = new ArrayList<>();
            for (String roleId : userRoles) {
                UserRole dbRole = userRoleDAO.getById(new BigInteger(roleId));
                dbRoles.add(dbRole);
            }
            user.setUserRoles(dbRoles);
        }
        userDAO.update(user);
    }
}