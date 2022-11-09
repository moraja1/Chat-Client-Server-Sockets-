package data.dao;

import data.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class DAO<T>{
    private Class<T> typeOfT;
    protected final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    @SuppressWarnings("unchecked")
    protected DAO(){
        this.typeOfT = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public boolean exists(Long key){
        Transaction transaction = null;
        try(Session session = sessionFactory.openSession()){
            transaction = session.beginTransaction();
            T obj = session.find(typeOfT, key);
            transaction.commit();
            if (obj == null){
                return false;
            }
        }catch(Exception ex){
            if (transaction != null)
                transaction.rollback();
            ex.printStackTrace();
        }
        return true;
    }
    public boolean add(T obj){
        Transaction transaction = null;
        Session session;
        try{
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.persist(obj);
            transaction.commit();
            session.close();
        }catch(Exception ex){
            if (transaction.isActive()) {
                transaction.rollback();
            }
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean erase(T obj){
        Transaction transaction = null;
        Session session;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.remove(obj);
            transaction.commit();
            session.close();
        }catch(Exception ex){
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean edit(T obj){
        Transaction transaction = null;
        try(Session session = sessionFactory.openSession()){
            transaction = session.beginTransaction();
            session.merge(obj);
            transaction.commit();
        }catch(Exception ex){
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    public abstract List<T> getAllObjects();
    public abstract T getSingleObject(Integer key);
}
