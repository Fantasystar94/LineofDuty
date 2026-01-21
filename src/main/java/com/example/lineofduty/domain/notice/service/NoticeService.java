package com.example.lineofduty.domain.notice.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.common.model.enums.Role;
import com.example.lineofduty.domain.notice.Notice;
import com.example.lineofduty.domain.notice.dto.NoticeDto;
import com.example.lineofduty.domain.notice.dto.response.NoticeInquiryListResponse;
import com.example.lineofduty.domain.notice.dto.response.NoticeInquiryResponse;
import com.example.lineofduty.domain.notice.dto.response.NoticeUpdateResponse;
import com.example.lineofduty.domain.notice.repository.NoticeRepository;
import com.example.lineofduty.domain.notice.dto.request.NoticeResisterRequest;
import com.example.lineofduty.domain.notice.dto.response.NoticeResisterResponse;
import com.example.lineofduty.domain.user.User;
import com.example.lineofduty.domain.user.dto.UserDetail;
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

    //공지사항 등록
    @Transactional
    public NoticeResisterResponse noticeResister(UserDetail userDetails, NoticeResisterRequest request) {

       User user = userPermissionCheck(userDetails);

        //2.공지사항 등록
        Notice notice = new Notice(request.getTitle(), request.getContent(), user);

        noticeRepository.save(notice);

        return NoticeResisterResponse.from(notice);
    }
    //공지사항 상세 조회
    @Transactional(readOnly = true)
    public NoticeInquiryResponse noticeInquiry(Long noticeId) {

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(
                () -> new CustomException(ErrorMessage.NOTICE_NOT_FOUND)
        );

        return NoticeInquiryResponse.from(notice);
    }

    //공지사항 페이징 조회
    @Transactional(readOnly = true)
    public NoticeInquiryListResponse noticeInquiryList(int page, int size, String sort) {

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

        Page<Notice> noticePage = noticeRepository.findAll(pageable);
        Page<NoticeDto> noticeDto = noticePage.map(NoticeDto::from);

        return NoticeInquiryListResponse.from(noticeDto);
    }

    //공지사항 수정
    @Transactional
    public NoticeUpdateResponse noticeUpdate(Long noticeId, UserDetail userDetails, NoticeResisterRequest request) {

        userPermissionCheck(userDetails);

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(
                () -> new CustomException(ErrorMessage.NOTICE_NOT_FOUND));

        notice.update(request.getTitle(),request.getContent());

        return NoticeUpdateResponse.from(notice);
    }

    //공지사항 삭제
    @Transactional
    public void noticeDelete(Long noticeId,UserDetail userDetails) {

        userPermissionCheck(userDetails);

         Notice notice = noticeRepository.findById(noticeId).orElseThrow(
                () -> new CustomException(ErrorMessage.NOTICE_NOT_FOUND));

        noticeRepository.delete(notice);
    }


    private User userPermissionCheck (UserDetail userDetails) {

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
