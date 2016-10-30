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

    public List<UserRole> getByRoleName(String... roleNames) {
        String query = "from UserRole where roleName in (<role-name>)";
        StringBuffer roleBuffer = new StringBuffer();
        for(int index = 0; index < roleNames.length ; index ++){
            roleBuffer.append("'" + roleNames[index] + "'");
            if(index !=  roleNames.length -1){
                roleBuffer.append(", ");
            }
        }
        query = query.replace("<role-name>", roleBuffer.toString());
        List<UserRole> roles = entityManager.createQuery(query).getResultList();
        if(roles != null)
            return roles;
        return null;
    }

    public List getRoles(){
        List list = entityManager.createQuery("select id, roleName from UserRole").getResultList();
        return list;
    }
}
