package com.xebia.dao;

import com.xebia.entities.ExcelMapping;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Pgupta on 02-08-2016.
 */
@Repository
@Transactional
public class ExcelMappingDAO {
    @PersistenceContext
    private EntityManager entityManager;

    public void create(ExcelMapping excelMapping) {
        entityManager.persist(excelMapping);
        return;
    }

    public List<ExcelMapping> getAll() {
        List<ExcelMapping> excelMappings = entityManager.createQuery("from ExcelMapping").getResultList();
        return excelMappings;
    }

    public void update(ExcelMapping excelMapping) {
        entityManager.merge(excelMapping);
        return;
    }
}
