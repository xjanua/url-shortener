package me.xjanua.spring.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import me.xjanua.spring.backend.model.ClickEvent;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {

    boolean existsByShortLink_IdAndIpHash(Long shortLinkId, String ipHash);
}