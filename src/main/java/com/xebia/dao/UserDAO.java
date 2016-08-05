package com.xebia.dao;

import com.xebia.entities.User;
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
public class UserDAO {
    @PersistenceContext
    private EntityManager entityManager;

    public void create(User user) {
        entityManager.persist(user);
        return;
    }

    /**
     * Return all the Users stored in the database.
     */
    public List<User> getAll() {
        List<User> users = entityManager.createQuery("from User").getResultList();
        return users;
    }

    public User getUserByUNamePwd(String userName, String password){
        List<User> users = entityManager.createQuery("from User where username =:username and password = :password")
                .setParameter("username", userName)
                .setParameter("password", password)
                .getResultList();
        User user = null;
        if(users != null && users.size() > 0){
            user = users.get(0);
        }
        return user;
    }

    public User getUserByUNameTkn(String userName, String token){
        User user = (User)entityManager.createQuery("from User where username =:username and token = :token")
                .setParameter("username", userName)
                .setParameter("token", token)
                .getSingleResult();
        return user;
    }

    public void update(User user) {
        entityManager.merge(user);
        return;
    }
}
