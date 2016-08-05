package com.xebia.dao;

/**
 * Created by Pgupta on 19-07-2016.
 */

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.Transactional;

import com.xebia.entities.Employee;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * This class is used to access data for the Employee entity.
 * Repository annotation allows the component scanning support to find and
 * configure the DAO wihtout any XML configuration and also provide the Spring
 * exceptiom translation.
 * Since we've setup setPackagesToScan and transaction manager on
 * DatabaseConfig, any bean method annotated with Transactional will cause
 * Spring to magically call begin() and commit() at the start/end of the
 * method. If exception occurs it will also call rollback().
 */
@Repository
@Transactional
public class EmployeeDAO {

    // ------------------------
    // PUBLIC METHODS
    // ------------------------

    /**
     * Save the Employee in the database.
     */
    public void create(Employee Employee) {
        entityManager.persist(Employee);
        return;
    }

    public long countEmployee(String eCode) {
        return (long) entityManager.createQuery("SELECT count(e) from Employee e where e_code = :eCode")
                .setParameter("eCode", eCode)
                .getSingleResult();
    }

    /**
     * Delete the Employee from the database.
     */
    public void delete(Employee Employee) {
        if (entityManager.contains(Employee))
            entityManager.remove(Employee);
        else
            entityManager.remove(entityManager.merge(Employee));
        return;
    }

    /**
     * Return all the Employees stored in the database.
     */
    @SuppressWarnings("unchecked")
    public List<Employee> getAll() {
        return entityManager.createQuery("from Employee").getResultList();
    }

    /**
     * Return the Employee having the passed email.
     */
    public Employee getByEmail(String email) {
        return (Employee) entityManager.createQuery("from Employee where email = :email")
                .setParameter("email", email)
                .getSingleResult();
    }

    /**
     * Return the Employee having the passed id.
     */
    public Employee getById(BigInteger id) {
        return entityManager.find(Employee.class, id);
    }

    public List<Employee> getApprovers() {
        return entityManager.createQuery("from Employee where approvals_required = 'N'").getResultList();
    }

    /**
     * Update the passed Employee in the database.
     */
    public void update(Employee Employee) {
        entityManager.merge(Employee);
        return;
    }


    /**
     * Return the Employee having the values.
     */
    public List<Employee> getByEmployeeObject(Employee employee) {
        StringBuffer conditions = new StringBuffer();
        String queryString = "from Employee ";
        boolean isFN = false, isLN = false, isEC = false, isDOJ = false, isE = false, isM = false;
        if (StringUtils.isNotBlank(employee.getFirstName())) {
            conditions.append("first_name = :firstName");
            isFN = true;
        }
        if (StringUtils.isNotBlank(employee.getLastName())) {
            if (isFN)
                conditions.append(" and ");
            conditions.append("last_name = :lastName");
            isLN = true;
        }
        if (StringUtils.isNotBlank(employee.getECode())) {
            if (isLN || isFN)
                conditions.append(" and ");
            conditions.append("e_code = :ecode");
            isEC = true;
        }
        if ((employee.getDateOfJoining() != null)) {
            if (isEC || isLN || isFN)
                conditions.append(" and ");
            conditions.append("date_of_joining = :doj");
            isDOJ = true;
        }
        if (StringUtils.isNotBlank(employee.getEmail())) {
            if (isDOJ || isEC || isLN || isFN)
                conditions.append(" and ");
            conditions.append("email = :email");
            isE = true;
        }

        if (StringUtils.isNotBlank(employee.getMobile())) {
            if (isE || isDOJ || isEC || isLN || isFN)
                conditions.append(" and ");
            conditions.append("mobile = :mobile");
            isM = true;
        }

        boolean isA = false;
        if (!StringUtils.equals(employee.getApprovalsRequired(), "NA")) {
            if (isE || isDOJ || isEC || isLN || isFN || isM)
                conditions.append(" and ");
            conditions.append("approvals_required = :approvalsRequired");
            isA = true;
        }

        if (conditions.length() > 0) {
            queryString += "where " + conditions.toString();
        }
        Query query = entityManager.createQuery(queryString);
        if (isFN)
            query.setParameter("firstName", employee.getFirstName());

        if (isLN)
            query.setParameter("lastName", employee.getLastName());

        if (isEC)
            query.setParameter("ecode", employee.getECode());

        if (isDOJ)
            query.setParameter("doj", employee.getDateOfJoining());

        if (isM)
            query.setParameter("mobile", employee.getMobile());

        if (isE)
            query.setParameter("email", employee.getEmail());
        if (isA)
            query.setParameter("approvalsRequired", employee.getApprovalsRequired());

        return query.getResultList();
    }

    // ------------------------
    // PRIVATE FIELDS
    // ------------------------

    // An EntityManager will be automatically injected from entityManagerFactory
    // setup on DatabaseConfig class.
    @PersistenceContext
    private EntityManager entityManager;

} // class EmployeeDao