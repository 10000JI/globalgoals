package dev.globalgoals.repository;

import dev.globalgoals.domain.Authority;
import dev.globalgoals.domain.Goal;
import dev.globalgoals.domain.StampCard;
import dev.globalgoals.domain.User;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
//@Repository
public class UserRepository_jpa_v {
    @PersistenceContext
    private EntityManager em;

    public void save(User user) {em.persist(user);}

    public Optional<User> findById(String id) {
        try {
            User user = em.createQuery("select m from User m where m.id=:id", User.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.ofNullable(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
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

        if (!rows.isEmpty()) {
            Object[] row = rows.get(0);
            User user = (User) row[0];
            Authority authority = (Authority) row[1];

            return User.builder()
                    .id(user.getId())
                    .password(user.getPassword())
                    .email(user.getEmail())
                    .name(user.getName())
                    .authorities(Collections.singleton(authority))
                    .build();
        }
        return null;
    }
    public List<Goal> findAllGoals() {
        return em.createQuery("SELECT g FROM Goal g", Goal.class)
                .getResultList();
    }

    public void stampSave(StampCard stampCard) {em.persist(stampCard);}

    public List<Object[]> stampFindById(String id) {
        return em.createQuery("select s,g from User m join m.stampCards s join s.goal g where m.id= :id")
                .setParameter("id", id)
                .getResultList();
    }

}
