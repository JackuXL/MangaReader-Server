package com.manga.controller;

import com.manga.dto.ApiResponse;
import com.manga.dto.MangaResponse;
import com.manga.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorites", description = "User favorites management endpoints")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final com.manga.repository.UserRepository userRepository;

    @PostMapping("/{mangaId}")
    @Operation(summary = "Add manga to favorites")
    public ResponseEntity<ApiResponse<Void>> addFavorite(
            @PathVariable Long mangaId,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            favoriteService.addFavorite(userId, mangaId);
            return ResponseEntity.ok(ApiResponse.success("Manga added to favorites", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{mangaId}")
    @Operation(summary = "Remove manga from favorites")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @PathVariable Long mangaId,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            favoriteService.removeFavorite(userId, mangaId);
            return ResponseEntity.ok(ApiResponse.success("Manga removed from favorites", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{mangaId}/status")
    @Operation(summary = "Check if manga is in favorites")
    public ResponseEntity<ApiResponse<Boolean>> isFavorite(
            @PathVariable Long mangaId,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        boolean isFavorite = favoriteService.isFavorite(userId, mangaId);
        return ResponseEntity.ok(ApiResponse.success(isFavorite));
    }

    @GetMapping
    @Operation(summary = "Get user's favorite manga")
    public ResponseEntity<ApiResponse<Page<MangaResponse>>> getUserFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        Pageable pageable = PageRequest.of(page, size);
        Page<MangaResponse> favorites = favoriteService.getUserFavorites(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(favorites));
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}


