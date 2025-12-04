package com.manga.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 新增章节请求
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChapterCreateRequest {
    
    /**
     * 漫画 ID
     */
    @NotNull(message = "Manga ID is required")
    private Long mangaId;
    
    /**
     * 章节标题
     */
    @NotBlank(message = "Chapter title is required")
    private String title;
    
    /**
     * 章节编号
     */
    @NotNull(message = "Chapter number is required")
    private Integer chapterNumber;
    
    /**
     * 页面 URL 列表（相对路径或完整 URL）
     * 例如：["manga/one-piece/ch1/page-1.jpg", "manga/one-piece/ch1/page-2.jpg"]
     */
    @NotEmpty(message = "At least one page URL is required")
    private List<String> pageUrls;
}

