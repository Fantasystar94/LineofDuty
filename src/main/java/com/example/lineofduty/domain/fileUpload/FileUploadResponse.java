package com.example.lineofduty.domain.fileUpload;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileUploadResponse {

    private String fileName;
    private String fileLocation;

    public FileUploadResponse (String fileName, String fileLocation) {
        this.fileName = fileName;
        this.fileLocation = fileLocation;
    }

    public String getUrl() {
        return fileLocation;
    }
}
