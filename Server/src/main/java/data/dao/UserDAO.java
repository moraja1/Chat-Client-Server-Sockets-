package data.dao;

import data.model.repository.User;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DAO<User>{
    @Override
    public List<User> getAllObjects() {
        List<User> users;
        Transaction transaction = null;
        Session session;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            TypedQuery<User> query = session.createNamedQuery("User.findAll", User.class);
            users = query.getResultList();
            transaction.commit();
            session.close();
        }catch(Exception ex){
            if (transaction != null) {
                transaction.rollback();
            }
            users = new ArrayList<>();
            ex.printStackTrace();
        }
        return users;
    }

    @Override
    public User getSingleObject(Long key) {
        User user = null;
        Transaction transaction = null;
        Session session;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            TypedQuery<User> query = session.createNamedQuery("User.findByIdUser", User.class);
            query.setParameter("IdUser", key);
            user = query.getSingleResult();
            transaction.commit();
            session.close();
        }catch(Exception ex){
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        }
        return user;
    }
    public User getSingleObject(String username, String password) {
        User user = null;
        Transaction transaction = null;
        Session session;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            TypedQuery<User> query = session.createNamedQuery("User.findByUsernameAndPassword", User.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            user = query.getSingleResult();
            transaction.commit();
            session.close();
        }catch(Exception ex){
            if (transaction != null) {
                transaction.rollback();
            }
        }
        return user;
    }
    public User getSingleObject(String username) {
        User user = null;
        Transaction transaction = null;
        Session session;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            TypedQuery<User> query = session.createNamedQuery("User.findByUsername", User.class);
            query.setParameter("username", username);
            user = query.getSingleResult();
            transaction.commit();
            session.close();
        }catch(Exception ex){
            if (transaction != null) {
                transaction.rollback();
            }
        }
        return user;
    }
}
