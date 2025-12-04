package com.manga.service;

import com.manga.config.AvatarProperties;
import com.manga.dto.AvatarResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvatarService {

    private final AvatarProperties avatarProperties;
    private final CdnService cdnService;

    /**
     * 获取头像完整 URL 列表
     * 
     * @return 头像 URL 列表
     */
    public AvatarResponse getAvatars() {
        return AvatarResponse.builder()
                .urls(cdnService.buildUrls(avatarProperties.getPaths()))
                .build();
    }
}
