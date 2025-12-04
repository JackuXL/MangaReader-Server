package com.manga.controller;

import com.manga.dto.ApiResponse;
import com.manga.dto.MangaImportRequest;
import com.manga.dto.MangaResponse;
import com.manga.service.ChapterService;
import com.manga.service.MangaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin management endpoints")
public class AdminController {

    private final MangaService mangaService;
    private final ChapterService chapterService;

    @PostMapping("/manga/import")
    @Operation(summary = "Import a single manga")
    public ResponseEntity<ApiResponse<MangaResponse>> importManga(
            @Valid @RequestBody MangaImportRequest request) {
        try {
            MangaResponse response = mangaService.importManga(request);
            return ResponseEntity.ok(ApiResponse.success("Manga imported successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/manga/batch-import")
    @Operation(summary = "Batch import multiple manga")
    public ResponseEntity<ApiResponse<List<MangaResponse>>> batchImportManga(
            @Valid @RequestBody List<MangaImportRequest> requests) {
        try {
            List<MangaResponse> responses = mangaService.batchImportManga(requests);
            return ResponseEntity.ok(ApiResponse.success(
                    "Successfully imported " + responses.size() + " manga", responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/manga/{id}")
    @Operation(summary = "Delete a manga")
    public ResponseEntity<ApiResponse<Void>> deleteManga(@PathVariable Long id) {
        try {
            mangaService.deleteManga(id);
            return ResponseEntity.ok(ApiResponse.success("Manga deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/chapters/{id}")
    @Operation(summary = "Delete a chapter")
    public ResponseEntity<ApiResponse<Void>> deleteChapter(@PathVariable Long id) {
        try {
            chapterService.deleteChapter(id);
            return ResponseEntity.ok(ApiResponse.success("Chapter deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}


