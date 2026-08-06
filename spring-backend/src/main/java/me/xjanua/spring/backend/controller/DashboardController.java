package me.xjanua.spring.backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.DailyClickCountDto;
import me.xjanua.spring.backend.dto.DashboardSummaryDto;
import me.xjanua.spring.backend.service.ClickEventService;
import me.xjanua.spring.backend.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ClickEventService clickEventService;
    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto> getSummary() {
        DashboardSummaryDto response = dashboardService.getCurrentUserSummary();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/daily-clicks")
    public ResponseEntity<List<DailyClickCountDto>> getDailyClicks(@RequestParam LocalDate from,
            @RequestParam LocalDate to) {
        List<DailyClickCountDto> response = clickEventService.getCurrentUserDailyClickCounts(from, to);
        return ResponseEntity.ok(response);
    }
}