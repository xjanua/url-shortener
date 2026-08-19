package me.xjanua.spring.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import me.xjanua.spring.backend.dto.DailyClickCountDto;
import me.xjanua.spring.backend.dto.shortLink.TopCountryDto;
import me.xjanua.spring.backend.model.ClickEvent;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {

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

        @Query("""
                        select new me.xjanua.spring.backend.dto.shortLink.TopCountryDto(
                            c.countryCode,
                            count(c.id)
                        )
                        from ClickEvent c
                        where c.shortLink.id = :shortLinkId
                          and c.isBot = false
                          and c.countryCode is not null
                          and c.countryCode <> ''
                        group by c.countryCode
                        order by count(c.id) desc, c.countryCode asc
                        """)
        List<TopCountryDto> findTopCountriesByShortLinkId(
                        @Param("shortLinkId") Long shortLinkId,
                        Pageable pageable);

        @Query("""
                        select case
                            when c.referrer is null or trim(c.referrer) = '' then 'Direct'
                            else c.referrer
                        end
                        from ClickEvent c
                        where c.shortLink.id = :shortLinkId
                          and c.isBot = false
                        group by case
                            when c.referrer is null or trim(c.referrer) = '' then 'Direct'
                            else c.referrer
                        end
                        order by count(c.id) desc
                        """)
        List<String> findTopReferrersByShortLinkId(
                        @Param("shortLinkId") Long shortLinkId,
                        Pageable pageable);

        List<ClickEvent> findByShortLink_IdAndIsBotFalseOrderByClickedAtDesc(Long shortLinkId, Pageable pageable);
}
