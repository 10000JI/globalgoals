package dev.globalgoals.repository;

import dev.globalgoals.domain.Goal;
import dev.globalgoals.domain.User;
import dev.globalgoals.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    List<User> findByEmail(String email);

    @Query("select u from User u join fetch u.authorities a where u.id = :id")
    User allFindById(@Param("id") String id);

    @Query("select s, g from User u join u.stampCards s join s.goal g where u.id = :id")
    List<Object[]> stampFindById(@Param("id") String id);

    @Query("select g from Goal g")
    List<Goal> findAllGoals();
}
