package com.xebia.dao;

import com.xebia.dto.AssetDto;
import com.xebia.dto.AssetTypeDto;
import com.xebia.entities.Asset;
import com.xebia.entities.AssetType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public long getTotalCount(){
        List list = entityManager.createQuery("select count(a) from Asset a").getResultList();
        return (long) list.get(0);
    }

    /**
     * Return all the Asset stored in the database.
     */
    public List<Asset> getAll(int offset, int limit) {
        List<Asset> assets = entityManager.createQuery("from Asset").setFirstResult(offset).setMaxResults(limit).getResultList();
        return assets;
    }

    public Map getByAssetObject(Asset asset, int offset, int limit){
        StringBuffer conditions = new StringBuffer();
        String queryString = "from Asset ";
        String countQueryString = "select count(a) from Asset a ";
        boolean isN = false, isSN = false, isM = false, isT = false;
        if (StringUtils.isNotBlank(asset.getName())) {
            conditions.append("name = :name");
            isN = true;
        }
        if (StringUtils.isNotBlank(asset.getSerialNumber())) {
            if (isN)
                conditions.append(" and ");
            conditions.append("serial_number = :sno");
            isSN = true;
        }
        if (asset.getAssetManufacturer() != null) {
            if (isN || isSN)
                conditions.append(" and ");
            conditions.append("manufacturer = :manu");
            isM = true;
        }

        if (asset.getAssetType() != null) {
            if (isN || isSN || isM)
                conditions.append(" and ");
            conditions.append("type = :type");
            isT = true;
        }
        if (conditions.length() > 0) {
            queryString += "where " + conditions.toString();
            countQueryString += "where " + conditions.toString();
        }
        Query query = entityManager.createQuery(queryString);
        Query countQuery = entityManager.createQuery(countQueryString);
        if (isN) {
            query.setParameter("name", asset.getName());
            countQuery.setParameter("name", asset.getName());
        }
        if (isSN) {
            query.setParameter("sno", asset.getSerialNumber());
            countQuery.setParameter("sno", asset.getSerialNumber());
        }
        if (isM) {
            query.setParameter("manu", asset.getAssetManufacturer());
            countQuery.setParameter("manu", asset.getAssetManufacturer());
        }
        if (isT) {
            query.setParameter("type", asset.getAssetType());
            countQuery.setParameter("type", asset.getAssetType());
        }

        Map resultMap = new HashMap<>();
        resultMap.put("result", query.setFirstResult(offset).setMaxResults(limit).getResultList());
        resultMap.put("count", (long)countQuery.getResultList().get(0));
        return resultMap;
    }

    public List<Asset> getByAssetDtoObject(AssetDto assetDto){
        StringBuffer conditions = new StringBuffer();
        String queryString = "from Asset ";
        boolean isN = false, isSN = false, isM = false, isT = false;
        if (StringUtils.isNotBlank(assetDto.getName())) {
            conditions.append("name = :name");
            isN = true;
        }
        if (StringUtils.isNotBlank(assetDto.getSerialNumber())) {
            if (isN)
                conditions.append(" and ");
            conditions.append("serial_number = :sno");
            isSN = true;
        }
        if (assetDto.getAssetManufacturer() != null) {
            if (isN || isSN)
                conditions.append(" and ");
            conditions.append("manufacturer = :manu");
            isM = true;
        }

        if (assetDto.getAssetType() != null) {
            if (isN || isSN || isM)
                conditions.append(" and ");
            conditions.append("type = :type");
            isT = true;
        }
        if (conditions.length() > 0) {
            queryString += "where " + conditions.toString();
        }
        Query query = entityManager.createQuery(queryString);
        if (isN)
            query.setParameter("name", assetDto.getName());

        if (isSN)
            query.setParameter("sno", assetDto.getSerialNumber());

        if (isM)
            query.setParameter("manu", assetDto.getAssetManufacturer());

        if (isT)
            query.setParameter("type", assetDto.getAssetType());

        return query.getResultList();
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

    public List<Asset> getAvailableByTypeId(BigInteger typId, BigInteger manuId) {

        return entityManager.createQuery("from Asset where type = :typeId and issued_to = null and manufacturer = :manuId")
                .setParameter("typeId",typId)
                .setParameter("manuId",manuId )
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
