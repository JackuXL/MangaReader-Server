package com.manga.controller;

import com.manga.dto.ApiResponse;
import com.manga.dto.BannerResponse;
import com.manga.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
@Tag(name = "Banner", description = "Banner/Carousel endpoints")
public class BannerController {

    private final BannerService bannerService;

    @GetMapping
    @Operation(summary = "Get all banners for carousel")
    public ResponseEntity<ApiResponse<List<BannerResponse>>> getBanners() {
        List<BannerResponse> banners = bannerService.getBanners();
        return ResponseEntity.ok(ApiResponse.success(banners));
    }
}

