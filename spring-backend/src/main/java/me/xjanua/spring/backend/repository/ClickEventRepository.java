package me.xjanua.spring.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import me.xjanua.spring.backend.dto.DailyClickCountDto;
import me.xjanua.spring.backend.model.ClickEvent;

public interface ClickEventRepository
        extends JpaRepository<ClickEvent, Long> {

    boolean existsByShortLink_IdAndIpHash(
            Long shortLinkId,
            String ipHash);

    @Query("""
            select new me.xjanua.spring.backend.dto.DailyClickCountDto(
                cast(c.clickedAt as LocalDate),
                count(c.id)
            )
            from ClickEvent c
            where c.clickedAt >= :from
              and c.clickedAt < :to
              and c.isBot = false
            group by cast(c.clickedAt as LocalDate)
            order by cast(c.clickedAt as LocalDate)
            """)
    List<DailyClickCountDto> countClicksByDay(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}