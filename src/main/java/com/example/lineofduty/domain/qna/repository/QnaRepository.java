package com.example.lineofduty.domain.qna.repository;

import com.example.lineofduty.domain.qna.Qna;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface QnaRepository extends JpaRepository<Qna, Long> {

     Optional<Qna> findById(Long qnaId);

    @Query("SELECT q FROM Qna q WHERE " +
            "(LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(q.questionContent) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Qna> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select q from Qna q where q.id = :id")
    Optional<Qna> findByIdWithPessimisticLock(@Param("id") Long id);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select q from Qna q where q.id = :id")
    Optional<Qna> findByIdWithOptimisticLock(@Param("id") Long id);
}
