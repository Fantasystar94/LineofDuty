package com.example.lineofduty.domain.qna.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.qna.dto.QnaDto;
import com.example.lineofduty.domain.qna.dto.request.QnaResisterRequest;
import com.example.lineofduty.domain.qna.dto.response.QnaInquiryResponse;
import com.example.lineofduty.domain.qna.dto.response.QnaResisterResponse;
import com.example.lineofduty.domain.qna.repository.QnaRepository;
import com.example.lineofduty.domain.user.repository.UserRepository;
import com.example.lineofduty.entity.Qna;
import com.example.lineofduty.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QnaService {

    private final QnaRepository qnaRepository;
    private final UserRepository userRepository;


    public QnaResisterResponse qnaRegistration(QnaResisterRequest request) {

//        User assignee = userRepository.findUserById((request.getAssigneeId());

        Qna qna = new Qna(
                request.getTitle(),
                request.getQuestionContent()
        );

        qnaRepository.save(qna);

        return new QnaResisterResponse(QnaDto.from(qna));
    }

    public QnaInquiryResponse qnaInquiry(Long id) {

        Qna qna = qnaRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorMessage.QUESTION_NOT_FOUND));

        return new QnaInquiryResponse(QnaDto.from(qna));
    }


}
