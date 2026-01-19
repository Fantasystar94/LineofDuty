package com.example.lineofduty.domain.notice.repository;

import com.example.lineofduty.domain.notice.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("SELECT n FROM Notice n WHERE n.isDeleted = false")
    Page<Notice> findAllByIsDeletedFalse(Pageable pageable);
}
