package com.example.lineofduty.domain.qna.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.notice.Notice;
import com.example.lineofduty.domain.qna.QnaDto;
import com.example.lineofduty.domain.qna.repository.QnaRepository;
import com.example.lineofduty.domain.qna.dto.request.QnaResisterRequest;
import com.example.lineofduty.domain.qna.dto.request.QnaUpdateRequest;
import com.example.lineofduty.domain.qna.dto.response.QnaInquiryListResponse;
import com.example.lineofduty.domain.qna.dto.response.QnaInquiryResponse;
import com.example.lineofduty.domain.qna.dto.response.QnaResisterResponse;
import com.example.lineofduty.domain.qna.dto.response.QnaUpdateResponse;
import com.example.lineofduty.domain.user.UserDetailsImpl;
import com.example.lineofduty.domain.qna.Qna;
import com.example.lineofduty.domain.user.repository.UserRepository;
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

    // 질문 등록
    public QnaResisterResponse qnaRegistration(UserDetailsImpl userDetails, QnaResisterRequest request) {

        if (userDetails.getUser().isDeleted()) {
            throw new CustomException(ErrorMessage.USER_DELETED_NOT_FOUND);
        }

        Qna qna = new Qna(request.getTitle(), request.getQuestionContent(), userDetails.getUser());

        qnaRepository.save(qna);

        return QnaResisterResponse.from(qna);
    }
    //질문 단건 조회
    @Transactional(readOnly = true)
    public QnaInquiryResponse qnaInquiry(Long qnaId) {

        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new CustomException(ErrorMessage.QUESTION_NOT_FOUND));

        return QnaInquiryResponse.from(qna);
    }

    //질문 목록 조회
    @Transactional(readOnly = true)
    public QnaInquiryListResponse qnaInquiryListResponse(int page, int size, String sort) {

        String sortProperty = "creatAt";
        Sort.Direction sortDirection = Sort.Direction.DESC;

        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            sortProperty = sortParams[0];
            if (sortParams.length > 1 && "asc".equalsIgnoreCase(sortParams[1])) {
                sortDirection = Sort.Direction.ASC;
            }
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortProperty));

        Page<Qna> qnaPage = qnaRepository.findAll(pageable);

        Page<QnaDto> qnaDtoPage = qnaPage.map(QnaDto::from);

        return QnaInquiryListResponse.from(qnaDtoPage);
    }

    //질문 수정
    public QnaUpdateResponse qnaUpdate(UserDetailsImpl userDetails, Long qnaId, QnaUpdateRequest request) {

        if (userDetails.getUser().isDeleted()) {
            throw new CustomException(ErrorMessage.USER_DELETED_NOT_FOUND);
        }

        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(()-> new CustomException(ErrorMessage.QUESTION_NOT_FOUND));

        if (!userDetails.getUser().getId().equals(qna.getUser().getId())) {
            throw new CustomException(ErrorMessage.NO_MODIFY_PERMISSION);
        }

        qna.update(request.getTitle(), request.getQuestionContent());

        return QnaUpdateResponse.from(qna);
    }

    //질문 삭제
    public void qnaDelete(UserDetailsImpl userDetails, Long qnaId) {

        if (userDetails.getUser().isDeleted()) {
            throw new CustomException(ErrorMessage.USER_DELETED_NOT_FOUND);
        }

        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(()-> new CustomException(ErrorMessage.QUESTION_NOT_FOUND));

        if (!userDetails.getUser().getId().equals(qna.getUser().getId())) {
            throw new CustomException(ErrorMessage.NO_MODIFY_PERMISSION);
        }

        qnaRepository.delete(qna);

    }

}
