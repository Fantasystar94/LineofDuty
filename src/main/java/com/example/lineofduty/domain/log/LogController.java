package com.example.lineofduty.domain.log;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import com.example.lineofduty.common.model.response.PageResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logs")
public class LogController {

    private final LogService logService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalResponse> getLogs(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 (예: createdAt,desc)") @RequestParam(defaultValue = "createdAt,desc") String sort,
            @Parameter(description = "검색할 유저 이름") @RequestParam(required = false) String username
    ) {
        Page<SystemLogResponse> logs = logService.searchLogs(page, size, sort, username);
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.LOG_READ_SUCCESS, PageResponse.from(logs)));
    }
}
