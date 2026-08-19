package me.xjanua.spring.backend.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import me.xjanua.spring.backend.model.ShortLink;

public interface ShortLinkRepository extends JpaRepository<ShortLink, Long>, JpaSpecificationExecutor<ShortLink> {

    Optional<ShortLink> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    long countByOwner_Id(UUID ownerId);

    long countByOwner_IdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            UUID ownerId,
            LocalDateTime from,
            LocalDateTime to);
}
