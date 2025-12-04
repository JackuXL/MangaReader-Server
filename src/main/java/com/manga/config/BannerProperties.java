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
 * 轮播图配置
 * 
 * 通过单个 JSON 环境变量配置轮播图数据
 * 
 * 环境变量配置示例:
 * BANNER_JSON=[{"image":"/banners/banner1.jpg","title":"热门推荐","link":"/manga/1","mangaId":1,"sortOrder":0,"enabled":true}]
 */
@Component
@Getter
@Setter
@Slf4j
public class BannerProperties {

    @Value("${banner.json:[]}")
    private String bannerJson;

    private List<BannerItem> items = new ArrayList<>();

    @PostConstruct
    public void init() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            items = mapper.readValue(bannerJson, new TypeReference<List<BannerItem>>() {});
            log.info("Loaded {} banner items from configuration", items.size());
        } catch (Exception e) {
            log.warn("Failed to parse banner JSON configuration: {}", e.getMessage());
            items = new ArrayList<>();
        }
    }

    @Getter
    @Setter
    public static class BannerItem {
        /**
         * 轮播图图片路径（相对路径，会自动拼接 CDN）
         */
        private String image;

        /**
         * 轮播图标题
         */
        private String title;

        /**
         * 点击跳转链接（可以是漫画详情页、分类页等）
         */
        private String link;

        /**
         * 关联的漫画 ID（可选，用于跳转到漫画详情）
         */
        private Long mangaId;

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
