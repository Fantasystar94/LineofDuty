package com.example.lineofduty.domain.qna.service;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.vane.badwordfiltering.BadWordFiltering;
import org.springframework.stereotype.Service;

@Service
public class ProfanityFilterServiceImpl implements ProfanityFilterService {

    // BadWordFiltering 인스턴스 생성 (라이브러리 사용)
    private final BadWordFiltering badWordFiltering = new BadWordFiltering();

    /**
     * 입력값에 비속어가 포함되어 있는지 여부 반환
     * @param text 검사할 문자열
     * @return 비속어 포함 시 true, 아니면 false
     */
    public boolean contains(String text) {
        return badWordFiltering.check(text);
    }

    /**
     * 입력값이 공란이거나, 비속어가 포함되어 있으면 예외 발생
     * @param text 검사할 문자열
     * @throws CustomException
     */
    public void validateNoProfanity(String text) {
        // 입력값이 공란인지 확인
//        if (badWordFiltering.blankCheck(text)) {
//            throw new CustomException(ErrorMessage.NOT_BLANK);
//        }
        // 비속어가 포함되어 있으면 예외 발생
        if (badWordFiltering.check(text)) {
            throw new CustomException(ErrorMessage.PROFANITY_DETECTED);
        }
    }
}
