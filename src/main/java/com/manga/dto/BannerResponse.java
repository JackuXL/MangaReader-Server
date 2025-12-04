package com.manga.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BannerResponse {
    
    /**
     * 轮播图图片完整 URL
     */
    private String imageUrl;

    /**
     * 轮播图标题
     */
    private String title;

    /**
     * 点击跳转链接
     */
    private String link;

    /**
     * 关联的漫画 ID
     */
    private Long mangaId;

    /**
     * 排序顺序
     */
    private Integer sortOrder;
}

