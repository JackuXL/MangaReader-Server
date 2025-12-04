package com.manga.service;

import com.manga.config.BannerProperties;
import com.manga.dto.BannerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerProperties bannerProperties;
    private final CdnService cdnService;

    /**
     * 获取所有启用的轮播图
     * 
     * @return 轮播图列表（按 sortOrder 排序）
     */
    public List<BannerResponse> getBanners() {
        return bannerProperties.getItems().stream()
                .filter(item -> item.getEnabled() != null && item.getEnabled())
                .filter(item -> StringUtils.hasText(item.getImage())) // 过滤空图片
                .sorted(Comparator.comparing(BannerProperties.BannerItem::getSortOrder, 
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private BannerResponse toResponse(BannerProperties.BannerItem item) {
        BannerResponse response = new BannerResponse();
        response.setImageUrl(cdnService.buildUrl(item.getImage()));
        response.setTitle(item.getTitle());
        response.setLink(item.getLink());
        response.setMangaId(item.getMangaId());
        response.setSortOrder(item.getSortOrder());
        return response;
    }
}

