package com.xebia.dao;

import com.xebia.dto.ActivityDTO;
import com.xebia.entities.EventMail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        eventMail.setEventDate(new Date());
        entityManager.persist(eventMail);
    }

    /*public void delete(EventMail eventMail) {
        if (entityManager.contains(eventMail))
            entityManager.remove(eventMail);
        else
            entityManager.remove(entityManager.merge(eventMail));
        return;
    }*/

    public long getCount() {
        List list = entityManager.createQuery("select count(e) from EventMail e").getResultList();
        return (long) list.get(0);
    }

    public Map getByActivityObject(ActivityDTO activityDTO) {
        StringBuffer conditions = new StringBuffer();
        String queryString = "from EventMail ";
        String countQueryString = "select count(e) from EventMail e ";
        boolean isU = false, isA = false, isFrom = false, isTo = false;
        if (activityDTO.getUser() != null && activityDTO.getUser().intValue() != -1) {
            conditions.append("user = :userId");
            isU = true;
        }
        if (StringUtils.isNotBlank(activityDTO.getAction())) {
            if (isU)
                conditions.append(" and ");
            conditions.append("event = :action");
            isA = true;
        }
        if ((activityDTO.getFrom() != null)) {
            if (isU || isA)
                conditions.append(" and ");
            conditions.append("event_date > :from");
            isFrom = true;
        }
        if ((activityDTO.getTo() != null)) {
            if (isU || isA || isFrom)
                conditions.append(" and ");
            conditions.append("event_date < :to");
            isTo = true;
        }

        if (conditions.length() > 0) {
            queryString += "where " + conditions.toString();
            countQueryString += "where " + conditions.toString();
        }

        Query query = entityManager.createQuery(queryString);
        Query countQuery = entityManager.createQuery(countQueryString);
        if (isU) {
            query.setParameter("userId", activityDTO.getUser());
            countQuery.setParameter("userId", activityDTO.getUser());
        }
        if (isA) {
            query.setParameter("action", activityDTO.getAction());
            countQuery.setParameter("action", activityDTO.getAction());
        }

        if (isFrom) {
            query.setParameter("from", activityDTO.getFrom());
            countQuery.setParameter("from", activityDTO.getFrom());
        }
        if (isTo) {
            query.setParameter("to", activityDTO.getTo());
            countQuery.setParameter("to", activityDTO.getTo());
        }

        Map resultMap = new HashMap<>();
        resultMap.put("result", query.setFirstResult(activityDTO.getOffset()).setMaxResults(activityDTO.getLimit()).getResultList());
        resultMap.put("count", (long)countQuery.getResultList().get(0));
        return resultMap;
    }
}
