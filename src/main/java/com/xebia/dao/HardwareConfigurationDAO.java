package com.xebia.dao;

import com.xebia.entities.HardwareConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by Pgupta on 25-07-2016.
 */
@Repository
@Transactional
public class HardwareConfigurationDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public void create(HardwareConfiguration hardwareConfiguration) {
        entityManager.persist(hardwareConfiguration);
        return;
    }

    public List<HardwareConfiguration> getAll() {
        List<HardwareConfiguration> hardwareConfigurations = entityManager.createQuery("from HardwareConfiguration").getResultList();
        return hardwareConfigurations;
    }

    public void delete(HardwareConfiguration hardwareConfiguration) {
        if (entityManager.contains(hardwareConfiguration))
            entityManager.remove(hardwareConfiguration);
        else
            entityManager.remove(entityManager.merge(hardwareConfiguration));
        return;
    }

    public HardwareConfiguration getById(BigInteger id) {
        return entityManager.find(HardwareConfiguration.class, id);
    }

    public List<HardwareConfiguration> getByObject(HardwareConfiguration hardwareConfiguration){
        StringBuffer conditions = new StringBuffer();
        String queryString = "from HardwareConfiguration ";
        boolean isCPU = false, isHDD = false, isRAM = false;
        if (StringUtils.isNotBlank(hardwareConfiguration.getCpu())) {
            conditions.append("cpu = :cpu");
            isCPU = true;
        }
        if (StringUtils.isNotBlank(hardwareConfiguration.getHdd())) {
            if (isCPU)
                conditions.append(" and ");
            conditions.append("hdd = :hdd");
            isHDD = true;
        }
        if (StringUtils.isNotBlank(hardwareConfiguration.getRam())) {
            if (isCPU || isHDD)
                conditions.append(" and ");
            conditions.append("ram = :ram");
            isRAM = true;
        }

        if (conditions.length() > 0) {
            queryString += "where " + conditions.toString();
        }
        Query query = entityManager.createQuery(queryString);
        if (isCPU)
            query.setParameter("cpu", hardwareConfiguration.getCpu());

        if (isRAM)
            query.setParameter("ram", hardwareConfiguration.getRam());

        if (isHDD)
            query.setParameter("hdd", hardwareConfiguration.getHdd());

        return query.getResultList();
    }

    public void update(HardwareConfiguration hardwareConfiguration) {
        entityManager.merge(hardwareConfiguration);
        return;
    }
}
