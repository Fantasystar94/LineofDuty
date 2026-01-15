package com.example.lineofduty.domain.notice.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.notice.dto.response.NoticeInquiryResponse;
import com.example.lineofduty.domain.notice.repository.NoticeRepository;
import com.example.lineofduty.domain.notice.dto.NoticeDto;
import com.example.lineofduty.domain.notice.dto.request.NoticeResisterRequest;
import com.example.lineofduty.domain.notice.dto.response.NoticeResisterResponse;
import com.example.lineofduty.domain.user.repository.UserRepository;
import com.example.lineofduty.entity.Notice;
import com.example.lineofduty.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {

    public final NoticeRepository noticeRepository;
    public final UserRepository userRepository;



    @Transactional
    public NoticeResisterResponse noticeResister(Long userId, NoticeResisterRequest request) {
        //1.관리자 권한인지 확인
//        User user = userRepository.findById(userId).orElseThrow(
//                () -> new CustomException(ErrorMessage.USER_NOT_FOUND)
//        );
//
//         if (user.getRole() != Role.ROLE_ADMIN) {
//             throw new CustomException(ErrorMessage.ADMIN_PERMISSION_REQUIRED);
//         }
        // 임시로 첫 번째 유저를 조회하여 할당 (테스트용)
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorMessage.USER_NOT_FOUND)
        );

        //2.공지사항 등록
        Notice notice = new Notice(
                request.getTitle(),
                request.getContent(),
                user
        );

        noticeRepository.save(notice);

        return new NoticeResisterResponse(NoticeDto.from(notice));
    }

    @Transactional(readOnly = true)
    public NoticeInquiryResponse noticeInquiry(Long noticeId) {

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(
                () -> new CustomException(ErrorMessage.NOTICE_NOT_FOUND)
        );

        return new NoticeInquiryResponse(NoticeDto.from(notice));
    }



}
