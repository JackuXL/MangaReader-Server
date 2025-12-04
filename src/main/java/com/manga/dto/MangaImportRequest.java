package com.manga.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MangaImportRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String oldName;
    
    private String description;
    
    @NotBlank(message = "Cover image URL is required")
    private String coverImageUrl;
    
    private List<String> detailImages;
    
    private String author;
    
    private String slogan;
    
    private Boolean isChoiceness;
    
    private Boolean isRecommend;
    
    private Boolean isNew;
    
    private String period;
    
    private Boolean isPutaway;
    
    private String putawayTime;
    
    private String onlineTime;
    
    private String tendency;
    
    private String country;
    
    private String isFinish;
    
    private Integer sortOrder;
    
    private Set<String> tags;
    
    private Set<String> labels;
    
    private String source;
    
    @Valid
    private List<ChapterImportRequest> chapters;
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChapterImportRequest {
        @NotBlank(message = "Chapter title is required")
        private String title;
        
        @NotNull(message = "Chapter number is required")
        private Integer chapterNumber;
        
        @NotEmpty(message = "At least one page is required")
        private List<String> pageUrls;
    }
}

