package com.manga.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementResponse {
    
    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 公告类型（info/warning/error/success）
     */
    private String type;

    /**
     * 点击跳转链接
     */
    private String link;

    /**
     * 排序顺序
     */
    private Integer sortOrder;
}

