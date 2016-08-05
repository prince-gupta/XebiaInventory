package com.xebia.dao;

import com.xebia.entities.AssignAssetMail;
import com.xebia.enums.MailStatus;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
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

    public void update(AssignAssetMail assignAssetMail) {
        entityManager.merge(assignAssetMail);
        return;
    }

    public void create(AssignAssetMail assignAssetMail){
        entityManager.persist(assignAssetMail);
    }

}
