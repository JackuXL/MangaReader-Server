package com.manga.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * CDN 配置属性
 * 
 * 用于管理 Cloudflare R2 或其他 CDN 的配置
 */
@Component
@ConfigurationProperties(prefix = "cdn")
@Getter
@Setter
public class CdnProperties {
    
    /**
     * CDN 基础 URL
     * 例如: https://your-bucket.r2.dev 或 https://cdn.yourdomain.com
     */
    private String baseUrl;
    
    /**
     * 是否启用 CDN URL 拼接
     * true: 返回完整 CDN URL
     * false: 返回相对路径
     */
    private boolean enabled = true;
    
    /**
     * 图片缓存时间（秒）
     * 用于设置 Cache-Control header
     */
    private long cacheMaxAge = 2592000; // 默认 30 天
    
    /**
     * 备用 CDN 域名（可选）
     * 用于容错或负载均衡
     */
    private String fallbackUrl;
}

