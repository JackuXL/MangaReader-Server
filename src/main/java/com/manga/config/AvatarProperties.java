package com.manga.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 头像配置
 * 
 * 通过单个 JSON 环境变量配置头像数据
 * 
 * 环境变量配置示例:
 * AVATAR_JSON={"prefix":"avatars/","suffix":".png","names":["avatar1","avatar2","avatar3"]}
 * 
 * 将生成相对路径: ["avatars/avatar1.png", "avatars/avatar2.png", "avatars/avatar3.png"]
 * 然后通过 CdnService 拼接成完整 URL
 */
@Component
@Getter
@Setter
@Slf4j
public class AvatarProperties {

    @Value("${avatar.json:{}}")
    private String avatarJson;

    /**
     * 头像相对路径列表（由 prefix + name + suffix 生成）
     */
    private List<String> paths = new ArrayList<>();

    @PostConstruct
    public void init() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            AvatarConfig config = mapper.readValue(avatarJson, AvatarConfig.class);
            if (config != null && config.getNames() != null) {
                String prefix = config.getPrefix() != null ? config.getPrefix() : "";
                String suffix = config.getSuffix() != null ? config.getSuffix() : "";
                
                this.paths = config.getNames().stream()
                        .map(name -> prefix + name + suffix)
                        .collect(Collectors.toList());
            }
            log.info("Loaded {} avatar paths from configuration", paths.size());
        } catch (Exception e) {
            log.warn("Failed to parse avatar JSON configuration: {}", e.getMessage());
        }
    }

    @Getter
    @Setter
    private static class AvatarConfig {
        private String prefix;
        private String suffix;
        private List<String> names;
    }
}
