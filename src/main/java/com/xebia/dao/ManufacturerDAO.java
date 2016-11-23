package com.xebia.dao;

import com.xebia.entities.AssetManufacturer;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by Pgupta on 24-07-2016.
 */
@Repository
@Transactional
public class ManufacturerDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public void create(AssetManufacturer manufacturer) {
        entityManager.persist(manufacturer);
        return;
    }

    public List<AssetManufacturer> getAll() {
        List<AssetManufacturer> assetManufacturers = entityManager.createQuery("from AssetManufacturer where deleted = 'N'").getResultList();
        return assetManufacturers;
    }

    public void delete(AssetManufacturer assetManufacturer) {
        if (entityManager.contains(assetManufacturer))
            entityManager.remove(assetManufacturer);
        else
            entityManager.remove(entityManager.merge(assetManufacturer));
        return;
    }

    public List<AssetManufacturer> getByName(String name){
        return entityManager.createQuery("from AssetManufacturer where name = :name").setParameter("name", name).getResultList();
    }

    public AssetManufacturer getById(BigInteger id) {
        return entityManager.find(AssetManufacturer.class, id);
    }

    public void update(AssetManufacturer assetManufacturer) {
        entityManager.merge(assetManufacturer);
        return;
    }

    public void markedDeleted(AssetManufacturer assetManufacturer){
        assetManufacturer.setDeleted("Y");
        update(assetManufacturer);
    }
}

