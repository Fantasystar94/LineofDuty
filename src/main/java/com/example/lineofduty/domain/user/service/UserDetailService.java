package com.example.lineofduty.domain.user.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.example.lineofduty.domain.user.User;
import com.example.lineofduty.domain.user.dto.UserDetail;
import com.example.lineofduty.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetail loadUserByUsername(String email) throws UsernameNotFoundException {

        // DB에서 유저 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일의 유저를 찾을 수 없습니다: " + email));

        // 탈퇴 여부 체크
        if (user.isDeleted()) {
            throw new CustomException(ErrorMessage.USER_DELETED_NOT_FOUND);
        }


        return new UserDetail(user);
    }

    public UserDetail loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("해당 ID의 유저를 찾을 수 없습니다: " + id));

        if (user.isDeleted()) {
            throw new CustomException(ErrorMessage.USER_WITHDRAWN);
        }

        return new UserDetail(user);
    }

}