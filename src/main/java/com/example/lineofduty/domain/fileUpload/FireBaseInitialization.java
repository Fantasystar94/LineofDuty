package com.example.lineofduty.domain.fileUpload;

import com.example.lineofduty.common.exception.CustomException;
import com.example.lineofduty.common.exception.ErrorMessage;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class FireBaseInitialization {

    private final GoogleCredentials credentials;

    //만들어진 Bean 주입
    public FireBaseInitialization(GoogleCredentials credentials) {
        this.credentials = credentials;
    }

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(credentials)    //json 받아서 생성함.
                        .build();

                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            throw new CustomException(ErrorMessage.INVALID_REQUEST);
        }
    }
}