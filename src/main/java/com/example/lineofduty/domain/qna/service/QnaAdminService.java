package com.example.lineofduty.domain.qna.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.common.model.enums.Role;
import com.example.lineofduty.domain.qna.Qna;
import com.example.lineofduty.domain.qna.repository.QnaRepository;
import com.example.lineofduty.domain.qna.dto.request.QnaAdminAnswerRequest;
import com.example.lineofduty.domain.qna.dto.request.QnaAdminAnswerUpdateRequest;
import com.example.lineofduty.domain.qna.dto.response.QnaAdminAnswerResponse;
import com.example.lineofduty.domain.qna.dto.response.QnaAdminAnswerUpdateResponse;
import com.example.lineofduty.domain.user.UserDetail;
import com.example.lineofduty.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QnaAdminService {

    private final QnaRepository qnaRepository;


    // 질문 관리자 답변 등록
    @Transactional
    public QnaAdminAnswerResponse qnaAdminAnswer(Long qnaId, UserDetail userDetails, QnaAdminAnswerRequest request)  {

        userPermissionCheck(userDetails);

        Qna qna = qnaRepository.findById(qnaId).orElseThrow(
                () -> new CustomException(ErrorMessage.QUESTION_NOT_FOUND));

        qna.createAnswer(request.getAskContent());

        return QnaAdminAnswerResponse.from(qna);
    }

    // 질문 관리자 답변 수정
    @Transactional
    public QnaAdminAnswerUpdateResponse qnaAdminAnswerUpdate(Long qnaId,UserDetail userDetails, QnaAdminAnswerUpdateRequest request) {

        userPermissionCheck(userDetails);

        Qna qna = qnaRepository.findById(qnaId).orElseThrow(
                () -> new CustomException(ErrorMessage.QUESTION_NOT_FOUND));

        qna.updateAnswer(request.getAskContent());

        return QnaAdminAnswerUpdateResponse.from(qna);
    }

    //유저 권한,탈퇴 체크
    private User userPermissionCheck(UserDetail userDetails) {

        User user = userDetails.getUser();

        if (user.isDeleted()) {
            throw new CustomException(ErrorMessage.USER_DELETED_NOT_FOUND);
        }

        if (user.getRole() != Role.ROLE_ADMIN) {
            throw new CustomException(ErrorMessage.ADMIN_PERMISSION_REQUIRED);
        }
        return user;
    }

}
