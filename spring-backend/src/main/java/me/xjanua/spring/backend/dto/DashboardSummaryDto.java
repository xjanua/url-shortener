package me.xjanua.spring.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardSummaryDto {
    private Long totalShortLinks;
    private Long shortLinksCreatedToday;
    private Long totalClicks;
    private Long clicksToday;
}
