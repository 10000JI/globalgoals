package dev.globalgoals.repository;

import dev.globalgoals.domain.Authority;
import dev.globalgoals.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepository {
    @PersistenceContext
    private EntityManager em;

    public void save(User user) {em.persist(user);}

    public List<User> findById(String id) {
        return em.createQuery("select m from User m where m.id=:id", User.class)
                .setParameter("id", id)
                .getResultList();
    }

    public List<User> findByEmail(String email) {
        return em.createQuery("select m from User m where m.email=:email", User.class)
                .setParameter("email", email)
                .getResultList();
    }

    public User allFindById(String id) {

        List<Object[]> rows = em.createQuery("select m, a from User m join m.authorities a where m.id= :id")
                .setParameter("id", id)
                .getResultList();

        User sample1 = null;
        Authority sample2 = null;
        for (Object[] row : rows) {
            sample1 = (User) row[0];
            sample2 = (Authority) row[1];
        }

        return User.builder()
                .id(sample1.getId())
                .password(sample1.getPassword())
                .email(sample1.getEmail())
                .name(sample1.getName())
                .authorities(Collections.singleton(sample2))
                .build();
    }
}
