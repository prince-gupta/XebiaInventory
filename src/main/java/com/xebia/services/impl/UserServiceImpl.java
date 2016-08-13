package com.xebia.services.impl;

import com.xebia.common.Utility;
import com.xebia.dao.EmployeeDAO;
import com.xebia.dao.UserDAO;
import com.xebia.dto.UserDto;
import com.xebia.entities.Employee;
import com.xebia.entities.User;
import com.xebia.entities.UserRole;
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

        Employee employee = new Employee();
        employee.setECode(userDto.getEcode());
        employee.setApprovalsRequired("NA");
        List<Employee> dbEmployeeList = employeeDAO.getByEmployeeObject(employee);
        if (dbEmployeeList.size() == 0)
            throw new ApplicationException("EC_Error:NA");

        User userAlreadyPresent = userDAO.getUserByEmployee(dbEmployeeList.get(0));
        if(userAlreadyPresent != null)
            throw new ApplicationException("EC_PRESENT_ERROR:"+userAlreadyPresent.getUsername());

        String enCodedPass = Utility.encode("Xebia123");
        User user = new User();
        user.setPassword(enCodedPass);
        user.setActive("Y");
        user.setUsername(userDto.getUserName());
        user.setEmployee(dbEmployeeList.get(0));
        userDAO.create(user);
        user.setPassword("Xebia123");
        return user;
    }

    private boolean checkIfUserNameAlreadyPresent(String uName) {
        return userDAO.getUserByUName(uName) != null;
    }

    public void resetPassword(String username) throws ApplicationException{
        User user = userDAO.getUserByUName(username);
        if(user == null){
            throw  new ApplicationException("UserNotFound");
        }
        String enCodedPass = Utility.encode("Xebia123");
        user.setPassword(enCodedPass);
        user.setToken(null);
        user.setChangePassword("Y");
        userDAO.update(user);
    }
}