package com.example.lineofduty.domain.notice.repository;

import com.example.lineofduty.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
