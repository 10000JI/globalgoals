package dev.globalgoals.repository;

import dev.globalgoals.domain.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCommentRepository extends JpaRepository<BoardComment, String> {
}
