package com.xebia.dao;

import com.xebia.enums.AssetStatus;
import com.xebia.entities.AssetHistory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by Pgupta on 25-07-2016.
 */

@Repository
@Transactional
public class AssetHistoryDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public void create(AssetHistory type) {
        entityManager.persist(type);
        return;
    }

    public List<AssetHistory> getHistory(BigInteger employeeID, BigInteger assetID, AssetStatus status){
        return entityManager.createQuery("from AssetHistory where issued_to = :issuedTo and asset_id = :assetId and status = :status")
                .setParameter("issuedTo", employeeID)
                .setParameter("assetId",assetID)
                .setParameter("status", status.getValue())
                .getResultList();
    }

    public List<AssetHistory> getIssuedNExpiredHistory(BigInteger employeeID, BigInteger assetID){
        return entityManager.createQuery("from AssetHistory where issued_to = :issuedTo and asset_id = :assetId and status = 'ISSUED' or status = 'EXPIRED'")
                .setParameter("issuedTo", employeeID)
                .setParameter("assetId",assetID)
                .getResultList();
    }

    public List<AssetHistory> getHistoryByEmployee(BigInteger employeeID){
        return entityManager.createQuery("from AssetHistory where issued_to = :issuedTo")
                .setParameter("issuedTo", employeeID)
                .getResultList();
    }

    public AssetHistory getById(BigInteger id) {
        return entityManager.find(AssetHistory.class, id);
    }

    public List<AssetHistory> getGoingToExpireAssetHistory(Date expiryDate){
        String query = "FROM AssetHistory WHERE valid_till = :expiryDate and status ='ISSUED'";
        return entityManager.createQuery(query)
                .setParameter("expiryDate",expiryDate)
                .getResultList();
    }

    public List<AssetHistory> getExpiredAssetHistory(Date expiryDate){
        String query = "FROM AssetHistory WHERE valid_till < :expiryDate and status ='ISSUED' or status ='EXPIRED'";
        return entityManager.createQuery(query)
                .setParameter("expiryDate",expiryDate)
                .getResultList();
    }

    public void update(AssetHistory assetHistory) {
        entityManager.merge(assetHistory);
        return;
    }
}
