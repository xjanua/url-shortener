package me.xjanua.spring.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

  long countByShortLink_Owner_Id(UUID ownerId);

  long countByShortLink_Owner_IdAndClickedAtGreaterThanEqualAndClickedAtLessThan(
      UUID ownerId,
      LocalDateTime from,
      LocalDateTime to);

  @Query("""
      select new me.xjanua.spring.backend.dto.DailyClickCountDto(
          cast(c.clickedAt as LocalDate),
          count(c.id)
      )
      from ClickEvent c
      where c.shortLink.owner.id = :ownerId
        and c.clickedAt >= :from
        and c.clickedAt < :to
      group by cast(c.clickedAt as LocalDate)
      order by cast(c.clickedAt as LocalDate)
      """)
  List<DailyClickCountDto> countClicksByDay(
      @Param("ownerId") UUID ownerId,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to);
}
