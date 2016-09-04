package com.xebia.dao;

import com.xebia.entities.PageRole;
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
public class PageRoleDao {

    @PersistenceContext
    private EntityManager entityManager;

    public void create(PageRole role) {
        entityManager.persist(role);
        return;
    }

    /**
     * Return all the Users stored in the database.
     */
    public List<PageRole> getAll() {
        List<PageRole> pageRole = entityManager.createQuery("from PageRole").getResultList();
        return pageRole;
    }

    public PageRole getByName(String name){
        List<PageRole> pageRoles = entityManager.createQuery("from PageRole where name = :name").setParameter("name", name).getResultList();
        return (pageRoles != null && pageRoles.size() > 0) ? pageRoles.get(0) : null;
    }

    public void update(PageRole pageRole) {
        entityManager.merge(pageRole);
        return;
    }
}
