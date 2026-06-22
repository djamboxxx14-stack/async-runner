package com.example.asyncrunner.repository;

import com.example.asyncrunner.domain.Task;
import com.example.asyncrunner.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    
    // Убрали @Lock, так как FOR UPDATE SKIP LOCKED уже в SQL
    @Query(value = """
        SELECT * FROM tasks t
        WHERE t.status IN ('NEW', 'RETRYABLE')
        AND (t.next_attempt_at IS NULL OR t.next_attempt_at <= :now)
        ORDER BY t.created_at
        LIMIT :batchSize
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    List<Task> findTasksForProcessing(
        @Param("now") Instant now,
        @Param("batchSize") int batchSize
    );
    
    List<Task> findByStatus(TaskStatus status);
    
    Optional<Task> findByOrderId(UUID orderId);
}