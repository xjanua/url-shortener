package me.xjanua.spring.backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.xjanua.spring.backend.enums.ShortCodeType;
import me.xjanua.spring.backend.enums.ShortLinkStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "short_links")
public class ShortLink extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "original_url", length = 2048, nullable = false)
    private String originalUrl;

    @Column(name = "android_url", length = 2048)
    private String androidUrl;

    @Column(name = "ios_url", length = 2048)
    private String iosUrl;

    @Column(name = "desktop_url", length = 2048)
    private String desktopUrl;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ShortLinkStatus status;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "short_code", length = 32, nullable = false, unique = true)
    private String shortCode;

    @Column(name = "password", length = 255)
    private String password;

    @Builder.Default
    @Column(name = "short_code_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ShortCodeType shortCodeType = ShortCodeType.GENERATED;

    @Builder.Default
    @OneToMany(mappedBy = "shortLink", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ClickEvent> clickEvents = new ArrayList<>();

    @Builder.Default
    @Column(name = "click_count", nullable = false)
    private Long clickCount = 0L;

    @Builder.Default
    @Column(name = "unique_clicks", nullable = false)
    private Long uniqueClicks = 0L;
}
