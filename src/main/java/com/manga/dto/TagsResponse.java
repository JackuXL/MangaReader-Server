package com.manga.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 标签列表响应包装类
 * 用于解决 Redis 缓存序列化 List<String> 时的类型问题
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TagsResponse {
    private List<String> tags;
    
    public static TagsResponse of(List<String> tags) {
        return new TagsResponse(tags);
    }
}

