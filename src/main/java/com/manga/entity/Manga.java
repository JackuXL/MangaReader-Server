package com.manga.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "manga")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Manga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 200)
    private String oldName;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, length = 500)
    private String coverImageUrl;

    @ElementCollection
    @CollectionTable(name = "manga_detail_images", joinColumns = @JoinColumn(name = "manga_id"))
    @OrderColumn(name = "image_order")
    @Column(name = "image_url", length = 500)
    private List<String> detailImages = new ArrayList<>();

    @Column(length = 100)
    private String author;

    @Column(length = 200)
    private String slogan;

    @Column(nullable = false)
    private Boolean isChoiceness = false;

    @Column(nullable = false)
    private Boolean isRecommend = false;

    @Column(nullable = false)
    private Boolean isNew = false;

    @Column(length = 50)
    private String period;

    @Column(nullable = false)
    private Boolean isPutaway = true;

    @Column
    private LocalDateTime putawayTime;

    @Column
    private LocalDateTime onlineTime;

    @Column(length = 2)
    private String tendency;

    @Column(length = 2)
    private String country;

    @Column(length = 2)
    private String isFinish;

    @Column(nullable = false)
    private Integer sortOrder = 0;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @Column(nullable = false)
    private Integer favoriteCount = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "manga", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chapter> chapters = new ArrayList<>();

    @OneToMany(mappedBy = "manga", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Favorite> favorites = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "manga_tags", joinColumns = @JoinColumn(name = "manga_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "manga_labels", joinColumns = @JoinColumn(name = "manga_id"))
    @Column(name = "label")
    private Set<String> labels = new HashSet<>();

    @Column(length = 100)
    private String source;
}

