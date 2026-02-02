package com.example.lineofduty.domain.log;

import com.example.lineofduty.common.model.enums.SuccessMessage;
import com.example.lineofduty.common.model.response.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<GlobalResponse> getLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String username
    ) {
        Page<SystemLogResponse> logs = logService.searchLogs(page, size, sort, username);
        return ResponseEntity.ok(GlobalResponse.success(SuccessMessage.LOG_READ_SUCCESS, logs));
    }
}
