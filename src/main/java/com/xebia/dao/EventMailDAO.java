package com.xebia.dao;

import com.xebia.entities.EventMail;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by Pgupta on 20-09-2016.
 */

@Repository
@Transactional
public class EventMailDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public EventMail getById(BigInteger id){
        return entityManager.find(EventMail.class, id);
    }

    public List<EventMail> getMailObjectsByStatus(String... statusArray){
        String query = "from EventMail where status in (";
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

    public void update(EventMail eventMail) {
        entityManager.merge(eventMail);
        return;
    }

    public void create(EventMail eventMail){
        entityManager.persist(eventMail);
    }

    public void delete(EventMail eventMail) {
        if (entityManager.contains(eventMail))
            entityManager.remove(eventMail);
        else
            entityManager.remove(entityManager.merge(eventMail));
        return;
    }
}
