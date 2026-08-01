package me.xjanua.spring.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import me.xjanua.spring.backend.model.ShortLink;

public interface ShortLinkRepository extends JpaRepository<ShortLink, Long>, JpaSpecificationExecutor<ShortLink> {

    Optional<ShortLink> findByShortCode(String shortCode);
}
