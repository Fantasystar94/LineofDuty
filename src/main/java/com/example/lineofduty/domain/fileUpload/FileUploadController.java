package com.example.lineofduty.domain.fileUpload;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<GlobalResponse> fileUploadApi(@RequestParam("file") MultipartFile file) throws IOException, FirebaseAuthException, ExecutionException,InterruptedException {

        FileUploadResponse response = fileUploadService.fileUpload(file);

        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.FILE_UPLOAD_SUCCESS, response));
    }
}
