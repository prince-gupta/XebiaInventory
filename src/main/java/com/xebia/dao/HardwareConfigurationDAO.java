package com.xebia.dao;

import com.xebia.entities.HardwareConfiguration;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    public void update(HardwareConfiguration hardwareConfiguration) {
        entityManager.merge(hardwareConfiguration);
        return;
    }
}
