package com.xebia.dao;

import com.xebia.dto.AssetTypeDto;
import com.xebia.entities.Asset;
import com.xebia.entities.AssetType;
import com.xebia.entities.Employee;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pgupta on 23-07-2016.
 */
@Repository
@Transactional
public class AssetTypeDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Save the Employee in the database.
     */
    public void create(AssetType type) {
        entityManager.persist(type);
        return;
    }

    public List<AssetType> getAll() {
        return entityManager.createQuery("from AssetType where deleted = 'N'").getResultList();
    }

    /**
     * Delete the Asset Type from the database.
     */
    public void delete(AssetType assetType) {
        if (entityManager.contains(assetType))
            entityManager.remove(assetType);
        else
            entityManager.remove(entityManager.merge(assetType));
        return;
    }

    public AssetType getById(BigInteger id) {
        return entityManager.find(AssetType.class, id);
    }

    public List<AssetType> getByType(String type){
        List<AssetType> resultList = entityManager.createQuery("from AssetType where type = :type and deleted = 'N'")
                .setParameter("type", type)
                .getResultList();
        return resultList;
    }

    public List<AssetType> getByTypeWithoutDeletionFilter(String type){
        List<AssetType> resultList = entityManager.createQuery("from AssetType where type = :type")
                .setParameter("type", type)
                .getResultList();
        return resultList;
    }

    public void update(AssetType assetType) {
        entityManager.merge(assetType);
        return;
    }


}
