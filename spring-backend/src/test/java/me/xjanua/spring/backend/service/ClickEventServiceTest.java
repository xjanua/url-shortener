package me.xjanua.spring.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import me.xjanua.spring.backend.dto.DailyClickCountDto;
import me.xjanua.spring.backend.repository.ClickEventRepository;

class ClickEventServiceTest {

    private ClickEventRepository clickEventRepository;
    private ClickEventService clickEventService;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        clickEventRepository = mock(ClickEventRepository.class);
        clickEventService = new ClickEventService(clickEventRepository, mock(ShortLinkService.class));
        ownerId = UUID.randomUUID();
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken(ownerId.toString(), null, "ROLE_USER"));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserDailyClickCountsFillsMissingDaysWithZero() {
        LocalDate from = LocalDate.of(2026, 8, 25);
        LocalDate to = LocalDate.of(2026, 8, 29);
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toExclusive = to.plusDays(1).atStartOfDay();

        when(clickEventRepository.countClicksByDay(ownerId, fromDateTime, toExclusive))
                .thenReturn(List.of(
                        new DailyClickCountDto(LocalDate.of(2026, 8, 26), 4L),
                        new DailyClickCountDto(LocalDate.of(2026, 8, 29), 2L)));

        List<DailyClickCountDto> result = clickEventService.getCurrentUserDailyClickCounts(from, to);

        assertThat(result)
                .extracting(DailyClickCountDto::getDay, DailyClickCountDto::getCount)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(LocalDate.of(2026, 8, 25), 0L),
                        org.assertj.core.groups.Tuple.tuple(LocalDate.of(2026, 8, 26), 4L),
                        org.assertj.core.groups.Tuple.tuple(LocalDate.of(2026, 8, 27), 0L),
                        org.assertj.core.groups.Tuple.tuple(LocalDate.of(2026, 8, 28), 0L),
                        org.assertj.core.groups.Tuple.tuple(LocalDate.of(2026, 8, 29), 2L));
        verify(clickEventRepository).countClicksByDay(ownerId, fromDateTime, toExclusive);
    }

    @Test
    void getCurrentUserDailyClickCountsReturnsEveryDayWhenThereAreNoClicks() {
        LocalDate from = LocalDate.of(2026, 8, 27);
        LocalDate to = LocalDate.of(2026, 8, 29);

        when(clickEventRepository.countClicksByDay(
                ownerId,
                from.atStartOfDay(),
                to.plusDays(1).atStartOfDay()))
                .thenReturn(List.of());

        List<DailyClickCountDto> result = clickEventService.getCurrentUserDailyClickCounts(from, to);

        assertThat(result)
                .extracting(DailyClickCountDto::getDay, DailyClickCountDto::getCount)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(LocalDate.of(2026, 8, 27), 0L),
                        org.assertj.core.groups.Tuple.tuple(LocalDate.of(2026, 8, 28), 0L),
                        org.assertj.core.groups.Tuple.tuple(LocalDate.of(2026, 8, 29), 0L));
    }
}
