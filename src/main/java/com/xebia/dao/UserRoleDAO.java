package com.xebia.dao;

import com.xebia.entities.UserRole;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by Pgupta on 29-08-2016.
 */
@Repository
@Transactional
public class UserRoleDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public void create(UserRole userRole) {
        entityManager.persist(userRole);
        return;
    }

    public List<UserRole> getAll() {
        List<UserRole> userRoles = entityManager.createQuery("from UserRole").getResultList();
        return userRoles;
    }

    public void update(UserRole userRole) {
        entityManager.merge(userRole);
        return;
    }

    public UserRole getById(BigInteger id) {
        return entityManager.find(UserRole.class, id);
    }

    public UserRole getByRoleName(String roleName) {
        List<UserRole> roles = entityManager.createQuery("from UserRole where roleName =:role")
                .setParameter("role", roleName).getResultList();
        if(roles != null && roles.size() > 0)
            return roles.get(0);
        return null;
    }

    public List getRoles(){
        List list = entityManager.createQuery("select id, roleName from UserRole").getResultList();
        return list;
    }
}
