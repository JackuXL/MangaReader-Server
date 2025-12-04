package com.manga.controller;

import com.manga.dto.ApiResponse;
import com.manga.dto.AvatarResponse;
import com.manga.service.AvatarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/avatars")
@RequiredArgsConstructor
@Tag(name = "Avatar", description = "Avatar list endpoints")
public class AvatarController {

    private final AvatarService avatarService;

    @GetMapping
    @Operation(summary = "Get avatar list configuration")
    public ResponseEntity<ApiResponse<AvatarResponse>> getAvatars() {
        AvatarResponse avatars = avatarService.getAvatars();
        return ResponseEntity.ok(ApiResponse.success(avatars));
    }
}

