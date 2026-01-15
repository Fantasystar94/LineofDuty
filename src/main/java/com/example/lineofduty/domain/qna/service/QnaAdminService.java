package com.example.lineofduty.domain.qna.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.qna.repository.QnaRepository;
import com.example.lineofduty.domain.qna.dto.QnaDto;
import com.example.lineofduty.domain.qna.dto.request.QnaAdminAnswerRequest;
import com.example.lineofduty.domain.qna.dto.request.QnaAdminAnswerUpdateRequest;
import com.example.lineofduty.domain.qna.dto.response.QnaAdminAnswerResponse;
import com.example.lineofduty.domain.qna.dto.response.QnaAdminAnswerUpdateResponse;
import com.example.lineofduty.domain.user.repository.UserRepository;
import com.example.lineofduty.entity.Qna;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QnaAdminService {

    private final QnaService qnaService;
    private final UserRepository userRepository;
    private final QnaRepository qnaRepository;


    // 질문 관리자 답변 등록
    @Transactional
    public QnaAdminAnswerResponse qnaAdminAnswer(Long qnaId, QnaAdminAnswerRequest request)  {

        //1.관리자 권한인지 확인
//        User user = userRepository.findById(userId).orElseThrow(
//                () -> new CustomException(ErrorMessage.USER_NOT_FOUND)
//        );
//
//         if (user.getRole() != Role.ROLE_ADMIN) {
//             throw new CustomException(ErrorMessage.ADMIN_PERMISSION_REQUIRED);
//         }
        //2.질문글을 찾기
        Qna qna = qnaRepository.findById(qnaId).orElseThrow(
                () -> new CustomException(ErrorMessage.QUESTION_NOT_FOUND));

        //3.질문글에 답변 넣기
        qna.createAnswer(request.getAskContent());

        return new QnaAdminAnswerResponse(QnaDto.from(qna));
    }

    // 질문 관리자 답변 수정
    @Transactional
    public QnaAdminAnswerUpdateResponse qnaAdminAnswerUpdate(Long qnaId, QnaAdminAnswerUpdateRequest request) {

        //1.관리자 권한인지 확인
//        User user = userRepository.findById(userId).orElseThrow(
//                () -> new CustomException(ErrorMessage.USER_NOT_FOUND)
//        );
//
//         if (user.getRole() != Role.ROLE_ADMIN) {
//             throw new CustomException(ErrorMessage.ADMIN_PERMISSION_REQUIRED);
//         }

        Qna qna = qnaRepository.findById(qnaId).orElseThrow(
                () -> new CustomException(ErrorMessage.QUESTION_NOT_FOUND));

        qna.updateAnswer(request.getAskContent());

        return new QnaAdminAnswerUpdateResponse(QnaDto.from(qna));
    }

}
