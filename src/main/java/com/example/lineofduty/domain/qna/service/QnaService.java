package com.example.lineofduty.domain.qna.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.qna.dto.QnaDto;
import com.example.lineofduty.domain.qna.dto.request.QnaResisterRequest;
import com.example.lineofduty.domain.qna.dto.request.QnaUpdateRequest;
import com.example.lineofduty.domain.qna.dto.response.QnaInquiryListResponse;
import com.example.lineofduty.domain.qna.dto.response.QnaInquiryResponse;
import com.example.lineofduty.domain.qna.dto.response.QnaResisterResponse;
import com.example.lineofduty.domain.qna.dto.response.QnaUpdateResponse;
import com.example.lineofduty.domain.qna.repository.QnaRepository;
import com.example.lineofduty.domain.user.repository.UserRepository;
import com.example.lineofduty.entity.Qna;
import com.example.lineofduty.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QnaService {

    private final QnaRepository qnaRepository;
    private final UserRepository userRepository;

    // 질문 등록
    public QnaResisterResponse qnaRegistration(Long userId,QnaResisterRequest request) {

        // 임시로 첫 번째 유저를 조회하여 할당 (테스트용)
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorMessage.USER_NOT_FOUND)
        );

        Qna qna = new Qna(
                request.getTitle(),
                request.getQuestionContent(),
                user
        );

        qnaRepository.save(qna);

        return new QnaResisterResponse(QnaDto.from(qna));
    }
    //질문 단건 조회
    @Transactional(readOnly = true)
    public QnaInquiryResponse qnaInquiry(Long qnaId) {

        Qna qna = qnaRepository.findById(qnaId).orElseThrow(
                () -> new CustomException(ErrorMessage.QUESTION_NOT_FOUND));

        return new QnaInquiryResponse(QnaDto.from(qna));
    }

    //질문 목록 조회
    @Transactional(readOnly = true)
    public QnaInquiryListResponse qnaInquiryListResponse(int page, int size, String[] sort) {

        String sortField = sort[0];
        String sortDirection = sort.length > 1 ? sort[1] : "desc";
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        Page<Qna> qnaPage = qnaRepository.findAll(pageable);
        Page<QnaDto> qnaDtoPage = qnaPage.map(QnaDto::from);

        return QnaInquiryListResponse.from(qnaDtoPage);
    }

    //질문 수정
    public QnaUpdateResponse qnaUpdate(Long qnaId, QnaUpdateRequest request) {

        Qna qna = qnaRepository.findById(qnaId).orElseThrow(
                ()-> new CustomException(ErrorMessage.QUESTION_NOT_FOUND));

        qna.update(request.getTitle(), request.getQuestionContent());

        return new QnaUpdateResponse(QnaDto.from(qna));
    }

    //질문 삭제
    public void qnaDelete(Long qnaId) {

        Qna qna = qnaRepository.findById(qnaId).orElseThrow
                (() -> new CustomException(ErrorMessage.QUESTION_NOT_FOUND)
        );

        qnaRepository.delete(qna);

    }
}
