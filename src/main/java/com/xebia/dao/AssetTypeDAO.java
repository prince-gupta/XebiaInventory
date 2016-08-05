package com.xebia.dao;

import com.xebia.dto.AssetTypeDto;
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

    /**
     * Return all the Asset stored in the database.
     */
    public List<AssetTypeDto> getAll() {
        List<AssetTypeDto> resutList = new ArrayList<>();
        List<AssetType> assetTypes = entityManager.createQuery("from AssetType").getResultList();
        for(AssetType assetType : assetTypes){
            int count = entityManager.createQuery("from Asset where type = :assetType")
                    .setParameter("assetType",assetType.getType()).getResultList().size();
            AssetTypeDto assetTypeDto = new AssetTypeDto();
            assetTypeDto.setNumberOfAsset(count);
            assetTypeDto.setType(assetType.getType());
            assetTypeDto.setId(assetType.getId());
            resutList.add(assetTypeDto);
        }
    return resutList;
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

    public void update(AssetType assetType) {
        entityManager.merge(assetType);
        return;
    }



}
