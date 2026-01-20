package com.example.lineofduty.domain.qna.repository;

import com.example.lineofduty.domain.qna.Qna;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QnaRepository extends JpaRepository<Qna, Long> {

     Optional<Qna> findById(Long qnaId);

}
