package com.manga.service;

import com.manga.config.AnnouncementProperties;
import com.manga.dto.AnnouncementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementProperties announcementProperties;

    /**
     * 获取所有启用的公告
     * 
     * @return 公告列表（按 sortOrder 排序）
     */
    public List<AnnouncementResponse> getAnnouncements() {
        return announcementProperties.getItems().stream()
                .filter(item -> item.getEnabled() != null && item.getEnabled())
                .sorted(Comparator.comparing(AnnouncementProperties.AnnouncementItem::getSortOrder, 
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private AnnouncementResponse toResponse(AnnouncementProperties.AnnouncementItem item) {
        AnnouncementResponse response = new AnnouncementResponse();
        response.setTitle(item.getTitle());
        response.setContent(item.getContent());
        response.setType(item.getType());
        response.setLink(item.getLink());
        response.setSortOrder(item.getSortOrder());
        return response;
    }
}

