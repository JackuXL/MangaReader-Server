package com.manga.controller;

import com.manga.dto.ApiResponse;
import com.manga.dto.MangaResponse;
import com.manga.service.MangaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manga")
@RequiredArgsConstructor
@Tag(name = "Manga", description = "Manga management endpoints")
public class MangaController {

    private final MangaService mangaService;

    @GetMapping
    @Operation(summary = "Get all manga with pagination")
    public ResponseEntity<ApiResponse<Page<MangaResponse>>> getAllManga(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MangaResponse> manga = mangaService.getAllManga(pageable);
        return ResponseEntity.ok(ApiResponse.success(manga));
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest manga")
    public ResponseEntity<ApiResponse<Page<MangaResponse>>> getLatestManga(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MangaResponse> manga = mangaService.getLatestManga(pageable);
        return ResponseEntity.ok(ApiResponse.success(manga));
    }

    @GetMapping("/popular")
    @Operation(summary = "Get popular manga")
    public ResponseEntity<ApiResponse<Page<MangaResponse>>> getPopularManga(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MangaResponse> manga = mangaService.getPopularManga(pageable);
        return ResponseEntity.ok(ApiResponse.success(manga));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get manga by ID")
    public ResponseEntity<ApiResponse<MangaResponse>> getMangaById(@PathVariable Long id) {
        try {
            MangaResponse manga = mangaService.getMangaById(id);
            mangaService.incrementViewCount(id);
            return ResponseEntity.ok(ApiResponse.success(manga));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search manga by keyword with optional tag filter and sort")
    public ResponseEntity<ApiResponse<Page<MangaResponse>>> searchManga(
            @RequestParam String keyword,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false, defaultValue = "default") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MangaResponse> manga = mangaService.searchManga(keyword, tag, sort, pageable);
        return ResponseEntity.ok(ApiResponse.success(manga));
    }

    @GetMapping("/choiceness")
    @Operation(summary = "Get featured/curated manga")
    public ResponseEntity<ApiResponse<Page<MangaResponse>>> getChoicenessManga(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MangaResponse> manga = mangaService.getChoicenessManga(pageable);
        return ResponseEntity.ok(ApiResponse.success(manga));
    }

    @GetMapping("/recommended")
    @Operation(summary = "Get recommended manga")
    public ResponseEntity<ApiResponse<Page<MangaResponse>>> getRecommendedManga(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MangaResponse> manga = mangaService.getRecommendedManga(pageable);
        return ResponseEntity.ok(ApiResponse.success(manga));
    }

    @GetMapping("/new")
    @Operation(summary = "Get new manga releases")
    public ResponseEntity<ApiResponse<Page<MangaResponse>>> getNewManga(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MangaResponse> manga = mangaService.getNewManga(pageable);
        return ResponseEntity.ok(ApiResponse.success(manga));
    }

    @GetMapping("/country/{country}")
    @Operation(summary = "Get manga by country/region")
    public ResponseEntity<ApiResponse<Page<MangaResponse>>> getMangaByCountry(
            @PathVariable String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<MangaResponse> manga = mangaService.getMangaByCountry(country, pageable);
            return ResponseEntity.ok(ApiResponse.success(manga));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid country value"));
        }
    }

    @GetMapping("/tendency/{tendency}")
    @Operation(summary = "Get manga by target audience")
    public ResponseEntity<ApiResponse<Page<MangaResponse>>> getMangaByTendency(
            @PathVariable String tendency,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<MangaResponse> manga = mangaService.getMangaByTendency(tendency, pageable);
            return ResponseEntity.ok(ApiResponse.success(manga));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid tendency value"));
        }
    }

    @GetMapping("/{id}/related")
    @Operation(summary = "Get related manga (random recommendations)")
    public ResponseEntity<ApiResponse<List<MangaResponse>>> getRelatedWorks(
            @PathVariable Long id,
            @RequestParam(defaultValue = "6") int limit) {
        try {
            List<MangaResponse> relatedManga = mangaService.getRelatedWorks(id, limit);
            return ResponseEntity.ok(ApiResponse.success(relatedManga));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/tag/{tag}")
    @Operation(summary = "Get manga by tag")
    public ResponseEntity<ApiResponse<Page<MangaResponse>>> getMangaByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MangaResponse> manga = mangaService.getMangaByTag(tag, pageable);
        return ResponseEntity.ok(ApiResponse.success(manga));
    }

    @GetMapping("/tags")
    @Operation(summary = "Get all available tags")
    public ResponseEntity<ApiResponse<List<String>>> getAllTags() {
        List<String> tags = mangaService.getAllTags().getTags();
        return ResponseEntity.ok(ApiResponse.success(tags));
    }
}


