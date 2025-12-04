package com.manga.repository;

import com.manga.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserIdAndMangaId(Long userId, Long mangaId);
    boolean existsByUserIdAndMangaId(Long userId, Long mangaId);
    
    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId ORDER BY f.createdAt DESC")
    Page<Favorite> findByUserId(Long userId, Pageable pageable);
    
    void deleteByUserIdAndMangaId(Long userId, Long mangaId);
}


