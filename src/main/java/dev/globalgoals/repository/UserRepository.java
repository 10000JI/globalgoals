package dev.globalgoals.repository;

import dev.globalgoals.domain.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UserRepository {
    @PersistenceContext
    private EntityManager em;

    public void save(User user) {em.persist(user);}

    public List<User> findByName(String id) {
        return em.createQuery("select m from User m where m.id=:id", User.class)
                .setParameter("id", id)
                .getResultList();
    }
}
