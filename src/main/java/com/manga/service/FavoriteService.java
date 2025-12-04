package com.manga.service;

import com.manga.dto.MangaResponse;
import com.manga.entity.Favorite;
import com.manga.entity.Manga;
import com.manga.entity.User;
import com.manga.repository.FavoriteRepository;
import com.manga.repository.MangaRepository;
import com.manga.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final MangaRepository mangaRepository;
    private final MangaService mangaService;
    private final CdnService cdnService;

    @Transactional
    public void addFavorite(Long userId, Long mangaId) {
        if (favoriteRepository.existsByUserIdAndMangaId(userId, mangaId)) {
            throw new RuntimeException("Manga already in favorites");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Manga manga = mangaRepository.findById(mangaId)
                .orElseThrow(() -> new RuntimeException("Manga not found"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setManga(manga);
        favoriteRepository.save(favorite);

        mangaService.incrementFavoriteCount(mangaId);
    }

    @Transactional
    public void removeFavorite(Long userId, Long mangaId) {
        if (!favoriteRepository.existsByUserIdAndMangaId(userId, mangaId)) {
            throw new RuntimeException("Manga not in favorites");
        }

        favoriteRepository.deleteByUserIdAndMangaId(userId, mangaId);
        mangaService.decrementFavoriteCount(mangaId);
    }

    public boolean isFavorite(Long userId, Long mangaId) {
        return favoriteRepository.existsByUserIdAndMangaId(userId, mangaId);
    }

    public Page<MangaResponse> getUserFavorites(Long userId, Pageable pageable) {
        Page<Favorite> favorites = favoriteRepository.findByUserId(userId, pageable);
        return favorites.map(favorite -> MangaResponse.fromEntity(favorite.getManga(), cdnService));
    }
}


