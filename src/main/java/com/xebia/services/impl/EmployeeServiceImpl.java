package com.xebia.services.impl;

import com.xebia.dao.*;
import com.xebia.entities.*;
import com.xebia.exception.ApplicationException;
import com.xebia.services.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Pgupta on 13-08-2016.
 */
@Service
public class EmployeeServiceImpl implements IEmployeeService {

    @Autowired
    EmployeeDAO employeeDAO;

    @Autowired
    AssetDAO assetDAO;

    @Autowired
    AssetHistoryDAO assetHistoryDAO;

    @Autowired
    AssignAssetMailDAO assignAssetMailDAO;

    @Autowired
    UserDAO userDAO;

    public void delete(String eCode, String username) throws ApplicationException{
        Employee employee = new Employee();
        employee.setECode(eCode);
        employee.setApprovalsRequired("NA");
        List<Employee> employeeList = employeeDAO.getByEmployeeObject(employee);
        if(employeeList.size() == 0 ){
            throw new ApplicationException("NOT_FOUND");
        }
        User loggedInUser = userDAO.getUserByUName(username);

        if(loggedInUser.getEmployee().getECode().equals(employeeList.get(0).getECode())){
            throw new ApplicationException("LOGGEDIN_USER_ERROR");
        }
        List<Asset> employeeAssets = assetDAO.getByEmployeeId(employeeList.get(0).getId());

        if(employeeAssets.size() > 0) {
            throw new ApplicationException("ASSETS_PRESENT");
        }

        List<AssetHistory> assetHistoryList = assetHistoryDAO.getHistoryByEmployee(employeeList.get(0).getId());
        for(AssetHistory assetHistory : assetHistoryList){
            assetHistoryDAO.delete(assetHistory);
        }

        List<AssignAssetMail> assignAssetMails = assignAssetMailDAO.getMailObjectsByEmployeeId(employeeList.get(0));
        for(AssignAssetMail assignAssetMail : assignAssetMails){
            assignAssetMailDAO.delete(assignAssetMail);
        }

        employeeDAO.delete(employeeList.get(0));
    }
}
