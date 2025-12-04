package com.manga.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 公告配置
 * 
 * 通过单个 JSON 环境变量配置公告数据
 * 
 * 环境变量配置示例:
 * ANNOUNCEMENT_JSON=[{"title":"系统维护通知","content":"系统将于今晚进行维护","type":"info","link":"/notice/1","sortOrder":0,"enabled":true}]
 */
@Component
@Getter
@Setter
@Slf4j
public class AnnouncementProperties {

    @Value("${announcement.json:[]}")
    private String announcementJson;

    private List<AnnouncementItem> items = new ArrayList<>();

    @PostConstruct
    public void init() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            items = mapper.readValue(announcementJson, new TypeReference<List<AnnouncementItem>>() {});
            log.info("Loaded {} announcement items from configuration", items.size());
        } catch (Exception e) {
            log.warn("Failed to parse announcement JSON configuration: {}", e.getMessage());
            items = new ArrayList<>();
        }
    }

    @Getter
    @Setter
    public static class AnnouncementItem {
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
        private String type = "info";

        /**
         * 点击跳转链接（可选）
         */
        private String link;

        /**
         * 排序顺序（数字越小越靠前）
         */
        private Integer sortOrder = 0;

        /**
         * 是否启用
         */
        private Boolean enabled = true;
    }
}

