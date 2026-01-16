package com.example.lineofduty.domain.notice.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.notice.dto.response.NoticeInquiryListResponse;
import com.example.lineofduty.domain.notice.dto.response.NoticeInquiryResponse;
import com.example.lineofduty.domain.notice.dto.response.NoticeUpdateResponse;
import com.example.lineofduty.domain.notice.repository.NoticeRepository;
import com.example.lineofduty.domain.notice.dto.NoticeDto;
import com.example.lineofduty.domain.notice.dto.request.NoticeResisterRequest;
import com.example.lineofduty.domain.notice.dto.response.NoticeResisterResponse;
import com.example.lineofduty.domain.qna.dto.QnaDto;
import com.example.lineofduty.domain.qna.dto.response.QnaInquiryListResponse;
import com.example.lineofduty.domain.user.repository.UserRepository;
import com.example.lineofduty.entity.Notice;
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
public class NoticeService {

    public final NoticeRepository noticeRepository;
    public final UserRepository userRepository;



    //공지사항 등록
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
    //공지사항 상세 조회
    @Transactional(readOnly = true)
    public NoticeInquiryResponse noticeInquiry(Long noticeId) {

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(
                () -> new CustomException(ErrorMessage.NOTICE_NOT_FOUND)
        );

        return new NoticeInquiryResponse(NoticeDto.from(notice));
    }

    //공지사항 페이징 조회
    @Transactional(readOnly = true)
    public NoticeInquiryListResponse noticeInquiryList(int page, int size, String[] sort) {

        String sortField = sort[0];
        String sortDirection = sort.length > 1 ? sort[1] : "desc";
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);

        // PageRequest.of는 0부터 시작하므로, 사용자가 1페이지를 요청하면 0으로 변환
        int pageNumber = (page > 0) ? page - 1 : 0;

        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(direction, sortField));

        Page<Notice> noticePage = noticeRepository.findAllByIsDeletedFalse(pageable);
        Page<NoticeDto> noticeDtoPage = noticePage.map(NoticeDto::from);

        return NoticeInquiryListResponse.from(noticeDtoPage);
    }

    //공지사항 수정
    @Transactional
    public NoticeUpdateResponse noticeUpdate(Long noticeId, NoticeResisterRequest request) {

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(
                () -> new CustomException(ErrorMessage.NOTICE_NOT_FOUND));

        notice.update(request.getTitle(),request.getContent());

        return new NoticeUpdateResponse(NoticeDto.from(notice));
    }

    //공지사항 삭제
    @Transactional
    public void noticeDelete(Long noticeId) {

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(
                () -> new CustomException(ErrorMessage.NOTICE_NOT_FOUND));

        noticeRepository.delete(notice);
    }




}
