package com.example.lineofduty.domain.qna.repository;

import com.example.lineofduty.entity.Qna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface QnaRepository extends JpaRepository<Qna, Long> {

     Optional<Qna> findById(Long qnaId);

}
