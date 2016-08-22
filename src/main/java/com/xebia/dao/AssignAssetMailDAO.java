package com.xebia.dao;

import com.xebia.entities.Asset;
import com.xebia.entities.AssignAssetMail;
import com.xebia.entities.Employee;
import com.xebia.enums.MailStatus;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by Pgupta on 02-08-2016.
 */
@Repository
@Transactional
public class AssignAssetMailDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public List<AssignAssetMail> getMailObjectsByStatus(String... statusArray){
        String query = "from AssignAssetMail where status in (";
        String statusString = "";
        for(int index = 0; index < statusArray.length ; index ++){
            statusString += "'" + statusArray[index] + "'";
            if(index !=  statusArray.length -1){
                statusString += ", ";
            }
        }
        query += statusString + " )";
        return entityManager.createQuery(query)
                .getResultList();

    }

    public List<AssignAssetMail> getByAsset(Asset asset){
        return entityManager.createQuery("from AssignAssetMail where asset = :asset")
                .setParameter("asset", asset)
                .getResultList();
    }

    public List<AssignAssetMail> getMailObjectsByEmployeeId(Employee employee){
        return entityManager.createQuery("from AssignAssetMail where employee = :employee").setParameter("employee", employee).getResultList();
    }

    public void update(AssignAssetMail assignAssetMail) {
        entityManager.merge(assignAssetMail);
        return;
    }

    public void create(AssignAssetMail assignAssetMail){
        entityManager.persist(assignAssetMail);
    }

    public void delete(AssignAssetMail assetMail) {
        if (entityManager.contains(assetMail))
            entityManager.remove(assetMail);
        else
            entityManager.remove(entityManager.merge(assetMail));
        return;
    }

}
