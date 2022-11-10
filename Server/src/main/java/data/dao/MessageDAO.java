package data.dao;

import data.model.repository.Message;
import data.model.repository.User;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class MessageDAO extends DAO<Message> {
    @Override
    public List<Message> getAllObjects() {
        return null;
    }

    @Override
    public Message getSingleObject(Integer key) {
        return null;
    }

    public List<Message> getPendingMessages(User destinatary) {
        List<Message> messagesByUSer = new ArrayList<>();
        List<Message> pendingMessages = new ArrayList<>();
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
        if(!messagesByUSer.isEmpty()){
            for(Message m : messagesByUSer){
                if(!m.isDelivered()){
                    pendingMessages.add(m);
                }
            }
        }
        return pendingMessages;
    }
}
