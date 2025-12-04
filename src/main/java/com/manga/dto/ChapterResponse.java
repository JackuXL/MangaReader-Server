package com.manga.dto;

import com.manga.entity.Chapter;
import com.manga.service.CdnService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChapterResponse {
    private Long id;
    private Long mangaId;
    private String title;
    private Integer chapterNumber;
    private Integer viewCount;
    private List<String> pageUrls;
    private LocalDateTime createdAt;
    
    /**
     * 从实体转换为 DTO（不进行 CDN URL 转换）
     */
    public static ChapterResponse fromEntity(Chapter chapter) {
        return fromEntity(chapter, null);
    }

    /**
     * 从实体转换为 DTO（支持 CDN URL 动态拼接）
     * 
     * @param chapter 章节实体
     * @param cdnService CDN 服务（可选，为 null 则不进行 URL 转换）
     * @return ChapterResponse
     */
    public static ChapterResponse fromEntity(Chapter chapter, CdnService cdnService) {
        ChapterResponse response = new ChapterResponse();
        response.setId(chapter.getId());
        response.setMangaId(chapter.getManga().getId());
        response.setTitle(chapter.getTitle());
        response.setChapterNumber(chapter.getChapterNumber());
        response.setViewCount(chapter.getViewCount());
        
        // CDN URL 处理
        if (cdnService != null) {
            response.setPageUrls(cdnService.buildUrls(chapter.getPageUrls()));
        } else {
            response.setPageUrls(chapter.getPageUrls());
        }
        
        response.setCreatedAt(chapter.getCreatedAt());
        return response;
    }
}

