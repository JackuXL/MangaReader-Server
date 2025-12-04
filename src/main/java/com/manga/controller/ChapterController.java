package com.manga.controller;

import com.manga.dto.ApiResponse;
import com.manga.dto.ChapterCreateRequest;
import com.manga.dto.ChapterResponse;
import com.manga.service.ChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
@Tag(name = "Chapters", description = "Chapter management endpoints")
public class ChapterController {

    private final ChapterService chapterService;

    @GetMapping("/manga/{mangaId}")
    @Operation(summary = "Get all chapters for a manga")
    public ResponseEntity<ApiResponse<List<ChapterResponse>>> getChaptersByMangaId(@PathVariable Long mangaId) {
        List<ChapterResponse> chapters = chapterService.getChaptersByMangaId(mangaId);
        return ResponseEntity.ok(ApiResponse.success(chapters));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get chapter by ID")
    public ResponseEntity<ApiResponse<ChapterResponse>> getChapterById(@PathVariable Long id) {
        try {
            ChapterResponse chapter = chapterService.getChapterById(id);
            chapterService.incrementViewCount(id);
            return ResponseEntity.ok(ApiResponse.success(chapter));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/manga/{mangaId}/chapter/{chapterNumber}")
    @Operation(summary = "Get chapter by manga ID and chapter number")
    public ResponseEntity<ApiResponse<ChapterResponse>> getChapterByMangaIdAndNumber(
            @PathVariable Long mangaId,
            @PathVariable Integer chapterNumber) {
        try {
            ChapterResponse chapter = chapterService.getChapterByMangaIdAndNumber(mangaId, chapterNumber);
            chapterService.incrementViewCount(chapter.getId());
            return ResponseEntity.ok(ApiResponse.success(chapter));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Create a new chapter (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ChapterResponse>> createChapter(@Valid @RequestBody ChapterCreateRequest request) {
        try {
            ChapterResponse chapter = chapterService.createChapter(request);
            return ResponseEntity.ok(ApiResponse.success(chapter));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}


