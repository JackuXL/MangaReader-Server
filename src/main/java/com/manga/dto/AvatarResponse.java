package com.manga.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvatarResponse {
    
    /**
     * 完整的头像 URL 列表
     */
    private List<String> urls;
}
