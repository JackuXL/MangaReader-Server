package com.manga.dto;

import com.manga.entity.Manga;
import com.manga.service.CdnService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MangaResponse {
    private Long id;
    private String title;
    private String oldName;
    private String description;
    private String coverImageUrl;
    private List<String> detailImages;
    private String author;
    private String slogan;
    private Boolean isChoiceness;
    private Boolean isRecommend;
    private Boolean isNew;
    private String period;
    private Boolean isPutaway;
    private LocalDateTime putawayTime;
    private LocalDateTime onlineTime;
    private String tendency;
    private String country;
    private String isFinish;
    private Integer sortOrder;
    private Integer viewCount;
    private Integer favoriteCount;
    private Integer chapterCount;
    private Set<String> tags;
    private Set<String> labels;
    private String source;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 从实体转换为 DTO（不进行 CDN URL 转换）
     */
    public static MangaResponse fromEntity(Manga manga) {
        return fromEntity(manga, null);
    }

    /**
     * 从实体转换为 DTO（支持 CDN URL 动态拼接）
     * 
     * @param manga 漫画实体
     * @param cdnService CDN 服务（可选，为 null 则不进行 URL 转换）
     * @return MangaResponse
     */
    public static MangaResponse fromEntity(Manga manga, CdnService cdnService) {
        MangaResponse response = new MangaResponse();
        response.setId(manga.getId());
        response.setTitle(manga.getTitle());
        response.setOldName(manga.getOldName());
        response.setDescription(manga.getDescription());
        
        // CDN URL 处理
        if (cdnService != null) {
            response.setCoverImageUrl(cdnService.buildUrl(manga.getCoverImageUrl()));
            response.setDetailImages(cdnService.buildUrls(manga.getDetailImages()));
        } else {
            response.setCoverImageUrl(manga.getCoverImageUrl());
            response.setDetailImages(manga.getDetailImages());
        }
        
        response.setAuthor(manga.getAuthor());
        response.setSlogan(manga.getSlogan());
        response.setIsChoiceness(manga.getIsChoiceness());
        response.setIsRecommend(manga.getIsRecommend());
        response.setIsNew(manga.getIsNew());
        response.setPeriod(manga.getPeriod());
        response.setIsPutaway(manga.getIsPutaway());
        response.setPutawayTime(manga.getPutawayTime());
        response.setOnlineTime(manga.getOnlineTime());
        response.setTendency(manga.getTendency());
        response.setCountry(manga.getCountry());
        response.setIsFinish(manga.getIsFinish());
        response.setSortOrder(manga.getSortOrder());
        response.setViewCount(manga.getViewCount());
        response.setFavoriteCount(manga.getFavoriteCount());
        response.setChapterCount(manga.getChapters() != null ? manga.getChapters().size() : 0);
        response.setTags(manga.getTags());
        response.setLabels(manga.getLabels());
        response.setSource(manga.getSource());
        response.setCreatedAt(manga.getCreatedAt());
        response.setUpdatedAt(manga.getUpdatedAt());
        return response;
    }
}

