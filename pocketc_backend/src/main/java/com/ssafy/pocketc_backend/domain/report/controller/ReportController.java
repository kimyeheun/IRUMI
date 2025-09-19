package com.ssafy.pocketc_backend.domain.report.controller;

import com.ssafy.pocketc_backend.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class ReportController {

    private final ReportService reportService;

    private Integer userId(Principal principal) { return Integer.parseInt(principal.getName()); }

}
