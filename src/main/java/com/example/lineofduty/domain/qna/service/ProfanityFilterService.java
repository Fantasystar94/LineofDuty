package com.example.lineofduty.domain.qna.service;

public interface ProfanityFilterService {

    boolean contains(String text);
    void validateNoProfanity(String text);
}
