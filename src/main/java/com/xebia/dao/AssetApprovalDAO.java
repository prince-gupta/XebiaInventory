package com.xebia.dao;

import com.xebia.entities.AssetApproval;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by Pgupta on 02-08-2016.
 */
@Repository
@Transactional
public class AssetApprovalDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public void create(AssetApproval assetApproval) {
        entityManager.persist(assetApproval);
        return;
    }

    public List<AssetApproval> getAll() {
        List<AssetApproval> assetApprovals = entityManager.createQuery("from AssetApproval where status <> 'APPROVED'").getResultList();
        return assetApprovals;
    }

    public List<AssetApproval> getAll(BigInteger userId) {
        List<AssetApproval> assetApprovals = entityManager.createQuery("from AssetApproval where raised_by = :id")
                .setParameter("id", userId)
                .getResultList();
        return assetApprovals;
    }

    public List getApprovalsCount(String... statusArray){
        String query = "from AssetApproval where status in (";
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

    public void update(AssetApproval assetApproval) {
        entityManager.merge(assetApproval);
        return;
    }
    public AssetApproval getById(BigInteger id){
        return entityManager.find(AssetApproval.class, id);
    }

    public List<AssetApproval> getByStatus(String status){
        return entityManager.createQuery("from AssetApproval where status = :status").setParameter("status", status).getResultList();
    }

    public List<AssetApproval> getPendingApprovals(){
        Date today = new Date();
        return entityManager.createQuery("from AssetApproval where submitted_date < :today and status = 'SENT'")
                .setParameter("today", today)
                .getResultList();
    }

    public List getApprovalStateCounts(){
        return entityManager.createQuery("select ap.status, count(ap) from AssetApproval as ap group by ap.status").getResultList();
    }
}
