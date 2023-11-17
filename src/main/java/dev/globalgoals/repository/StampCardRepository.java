package dev.globalgoals.repository;

import dev.globalgoals.domain.StampCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StampCardRepository extends JpaRepository<StampCard, String> {
}
