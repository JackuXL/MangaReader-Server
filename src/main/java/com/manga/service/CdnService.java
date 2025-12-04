package com.manga.service;

import com.manga.config.CdnProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CDN 服务 - 处理 CDN URL 的动态拼接
 * 
 * 功能：
 * 1. 统一管理 CDN 域名
 * 2. 动态拼接完整 URL
 * 3. 支持批量处理
 * 4. 支持容错和备用域名
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CdnService {

    private final CdnProperties cdnProperties;

    /**
     * 将相对路径转换为完整 CDN URL
     * 
     * @param relativePath 相对路径，如 "manga/one-piece/cover.jpg" 或 "/manga/one-piece/cover.jpg"
     * @return 完整 CDN URL 或相对路径（取决于配置）
     */
    public String buildUrl(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return relativePath;
        }

        // 如果已经是完整 URL，直接返回
        if (isAbsoluteUrl(relativePath)) {
            return relativePath;
        }

        // 如果未启用 CDN，返回相对路径
        if (!cdnProperties.isEnabled()) {
            return relativePath;
        }

        // 拼接 CDN 基础 URL
        String baseUrl = cdnProperties.getBaseUrl();
        if (!StringUtils.hasText(baseUrl)) {
            log.warn("CDN base URL is not configured, returning relative path");
            return relativePath;
        }

        // 确保路径以 / 开头
        String path = relativePath.startsWith("/") ? relativePath : "/" + relativePath;
        
        // 确保 baseUrl 不以 / 结尾
        String normalizedBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;

        return normalizedBaseUrl + path;
    }

    /**
     * 批量转换 URL
     * 
     * @param relativePaths 相对路径列表
     * @return 完整 URL 列表
     */
    public List<String> buildUrls(List<String> relativePaths) {
        if (relativePaths == null) {
            return null;
        }
        return relativePaths.stream()
                .map(this::buildUrl)
                .collect(Collectors.toList());
    }

    /**
     * 从完整 URL 提取相对路径（用于保存到数据库）
     * 
     * @param fullUrl 完整 URL
     * @return 相对路径
     */
    public String extractRelativePath(String fullUrl) {
        if (!StringUtils.hasText(fullUrl)) {
            return fullUrl;
        }

        // 如果已经是相对路径，直接返回
        if (!isAbsoluteUrl(fullUrl)) {
            return fullUrl;
        }

        // 移除 CDN 基础 URL
        String baseUrl = cdnProperties.getBaseUrl();
        if (StringUtils.hasText(baseUrl) && fullUrl.startsWith(baseUrl)) {
            return fullUrl.substring(baseUrl.length());
        }

        // 尝试从备用 URL 提取
        String fallbackUrl = cdnProperties.getFallbackUrl();
        if (StringUtils.hasText(fallbackUrl) && fullUrl.startsWith(fallbackUrl)) {
            return fullUrl.substring(fallbackUrl.length());
        }

        // 如果无法识别，尝试提取路径部分
        try {
            java.net.URL url = new java.net.URL(fullUrl);
            return url.getPath();
        } catch (Exception e) {
            log.warn("Failed to extract relative path from URL: {}", fullUrl);
            return fullUrl;
        }
    }

    /**
     * 批量提取相对路径
     */
    public List<String> extractRelativePaths(List<String> fullUrls) {
        if (fullUrls == null) {
            return null;
        }
        return fullUrls.stream()
                .map(this::extractRelativePath)
                .collect(Collectors.toList());
    }

    /**
     * 获取带容错的 URL
     * 如果主 CDN 不可用，返回备用 CDN URL
     * 
     * @param relativePath 相对路径
     * @param useFallback 是否使用备用 URL
     * @return CDN URL
     */
    public String buildUrlWithFallback(String relativePath, boolean useFallback) {
        if (!useFallback || !StringUtils.hasText(cdnProperties.getFallbackUrl())) {
            return buildUrl(relativePath);
        }

        if (!StringUtils.hasText(relativePath) || isAbsoluteUrl(relativePath)) {
            return relativePath;
        }

        String path = relativePath.startsWith("/") ? relativePath : "/" + relativePath;
        String fallbackUrl = cdnProperties.getFallbackUrl();
        String normalizedUrl = fallbackUrl.endsWith("/") ? fallbackUrl.substring(0, fallbackUrl.length() - 1) : fallbackUrl;

        return normalizedUrl + path;
    }

    /**
     * 判断是否为绝对 URL
     */
    private boolean isAbsoluteUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }

    /**
     * 获取 CDN 缓存配置（用于响应 header）
     */
    public long getCacheMaxAge() {
        return cdnProperties.getCacheMaxAge();
    }
}

