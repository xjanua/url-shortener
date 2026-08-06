package me.xjanua.spring.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.DashboardSummaryDto;
import me.xjanua.spring.backend.util.SecurityUtil;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ShortLinkService shortLinkService;
    private final ClickEventService clickEventService;

    public DashboardSummaryDto getCurrentUserSummary() {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime startOfTomorrow = startOfToday.plusDays(1);

        return new DashboardSummaryDto(
                shortLinkService.countByOwnerId(ownerId),
                shortLinkService.countCreatedByOwnerId(ownerId, startOfToday, startOfTomorrow),
                clickEventService.countByOwnerId(ownerId),
                clickEventService.countByOwnerIdAndClickedAtBetween(ownerId, startOfToday, startOfTomorrow));
    }
}
