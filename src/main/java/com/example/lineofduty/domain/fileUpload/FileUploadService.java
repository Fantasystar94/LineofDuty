package com.example.lineofduty.domain.fileUpload;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import io.grpc.Context;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FileUploadService {

    private static Firestore dbFirestore;

    private final String firebaseBucket = "lineofdutyfileupload.firebasestorage.app";

    @Transactional
    public FileUploadResponse fileUpload(MultipartFile file) throws IOException {

        LocalDateTime now = LocalDateTime.now();
        String fileName = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm/ss"));

        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
        InputStream content = new ByteArrayInputStream(file.getBytes());

        Blob blob = bucket.create(fileName, content, file.getContentType());

        return new FileUploadResponse(fileName, blob.getMediaLink());

    }


}
