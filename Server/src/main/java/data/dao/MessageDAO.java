package data.dao;

import data.model.repository.Message;
import data.model.repository.User;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO extends DAO<Message> {
    @Override
    public List<Message> getAllObjects() {
        List<Message> messages;
        Transaction transaction = null;
        Session session;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            TypedQuery<Message> query = session.createNamedQuery("Message.findAll", Message.class);
            messages = query.getResultList();
            transaction.commit();
            session.close();
        }catch(Exception ex){
            if (transaction != null) {
                transaction.rollback();
            }
            messages = new ArrayList<>();
            ex.printStackTrace();
        }
        return messages;
    }

    @Override
    public Message getSingleObject(Long key) {
        return null;
    }
    public Message getSingleObject(String message, User remitent, User destinatary, Timestamp dateTime) {
        Message messagePersisted = null;
        Transaction transaction = null;
        Session session;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            TypedQuery<Message> query = session.createNamedQuery("Message.findByField", Message.class);
            query.setParameter("message", message);
            query.setParameter("remitent", remitent.getIdUser());
            query.setParameter("destinatary", destinatary.getIdUser());
            query.setParameter("dateTime", dateTime);
            messagePersisted = query.getSingleResult();
            transaction.commit();
            session.close();
        }catch(Exception ex){
            if (transaction != null) {
                transaction.rollback();
            }
        }
        return messagePersisted;
    }

    public List<Message> getPendingMessages(User destinatary) {
        List<Message> messagesByUSer = new ArrayList<>();
        Transaction transaction = null;
        Session session;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            TypedQuery<Message> query = session.createNamedQuery("Message.findByDestinatary", Message.class);
            query.setParameter("destinatary", destinatary);
            messagesByUSer = query.getResultList();
            transaction.commit();
            session.close();
        }catch(Exception ex){
            if (transaction != null) {
                transaction.rollback();
            }
            ex.printStackTrace();
        }
        return messagesByUSer;
    }
    public boolean erase(Long Id){
        Transaction transaction = null;
        Session session;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.remove(Id);
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
}
