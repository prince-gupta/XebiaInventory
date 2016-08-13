package com.xebia.dao;

import com.xebia.dto.AssetTypeDto;
import com.xebia.entities.Asset;
import com.xebia.entities.AssetType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pgupta on 24-07-2016.
 */
@Repository
@Transactional
public class AssetDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public void create(Asset type) {
        entityManager.persist(type);
        return;
    }

    /**
     * Return all the Asset stored in the database.
     */
    public List<Asset> getAll() {
        List<Asset> assets = entityManager.createQuery("from Asset").getResultList();
        return assets;
    }

    /**
     * Delete the Asset from the database.
     */
    public void delete(Asset asset) {
        if (entityManager.contains(asset))
            entityManager.remove(asset);
        else
            entityManager.remove(entityManager.merge(asset));
        return;
    }

    public Asset getById(BigInteger id) {
        return entityManager.find(Asset.class, id);
    }

    public List<Asset> getByEmployeeId(BigInteger id) {

        return entityManager.createQuery("from Asset where issued_to = :issuedTo")
                .setParameter("issuedTo",id)
                .getResultList();
    }

    public List<Asset> getByTypeId(BigInteger id) {

        return entityManager.createQuery("from Asset where type = :typeId")
                .setParameter("typeId",id)
                .getResultList();
    }

    public List<Asset> getAvailableByTypeId(BigInteger id) {

        return entityManager.createQuery("from Asset where type = :typeId and issued_to = null")
                .setParameter("typeId",id)
                .getResultList();
    }

    public List<Asset> getAllByAssetType(BigInteger id){
        return entityManager.createQuery("from Asset where type = :typeId")
                .setParameter("typeId",id)
                .getResultList();
    }


    public void update(Asset asset) {
        entityManager.merge(asset);
        return;
    }

    public List getGroupByManufacturer(BigInteger id){
        List l = entityManager.createQuery("select s.manufacturer, count(*) from (select * from assets where type = :typeId) as s group by s.manufacturer")
                .setParameter("typeId", id).getResultList();
        return l;
    }
}
