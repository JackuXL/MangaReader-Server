package com.manga.repository;

import com.manga.entity.Manga;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MangaRepository extends JpaRepository<Manga, Long> {
    
    @Query("SELECT m FROM Manga m WHERE m.isPutaway = true AND " +
           "(LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(m.oldName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(m.author) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Manga> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT DISTINCT m FROM Manga m JOIN m.tags t WHERE m.isPutaway = true AND t = :tag AND " +
           "(LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(m.oldName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(m.author) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Manga> searchByKeywordAndTag(@Param("keyword") String keyword, @Param("tag") String tag, Pageable pageable);
    
    @Query("SELECT m FROM Manga m WHERE m.isPutaway = true ORDER BY m.viewCount DESC")
    Page<Manga> findTopByViewCount(Pageable pageable);
    
    @Query("SELECT m FROM Manga m WHERE m.isPutaway = true ORDER BY m.favoriteCount DESC")
    Page<Manga> findTopByFavoriteCount(Pageable pageable);
    
    @Query("SELECT m FROM Manga m WHERE m.isPutaway = true ORDER BY m.createdAt DESC")
    Page<Manga> findLatest(Pageable pageable);
    
    @Query("SELECT m FROM Manga m WHERE m.isPutaway = true AND m.isChoiceness = true ORDER BY m.sortOrder ASC, m.createdAt DESC")
    Page<Manga> findChoiceness(Pageable pageable);
    
    @Query("SELECT m FROM Manga m WHERE m.isPutaway = true AND m.isRecommend = true ORDER BY m.sortOrder ASC, m.createdAt DESC")
    Page<Manga> findRecommended(Pageable pageable);
    
    @Query("SELECT m FROM Manga m WHERE m.isPutaway = true AND m.isNew = true ORDER BY m.createdAt DESC")
    Page<Manga> findNew(Pageable pageable);
    
    @Query("SELECT m FROM Manga m WHERE m.isPutaway = true AND m.country = :country ORDER BY m.sortOrder ASC, m.createdAt DESC")
    Page<Manga> findByCountry(@Param("country") String country, Pageable pageable);
    
    @Query("SELECT m FROM Manga m WHERE m.isPutaway = true AND m.tendency = :tendency ORDER BY m.sortOrder ASC, m.createdAt DESC")
    Page<Manga> findByTendency(@Param("tendency") String tendency, Pageable pageable);
    
    @Query("SELECT m FROM Manga m WHERE m.isPutaway = true ORDER BY m.sortOrder ASC, m.createdAt DESC")
    Page<Manga> findAllPutaway(Pageable pageable);
    
    @Query(value = "SELECT * FROM manga WHERE is_putaway = true AND id != :excludeId ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Manga> findRandomManga(@Param("excludeId") Long excludeId, @Param("limit") int limit);
    
    @Query("SELECT DISTINCT m FROM Manga m JOIN m.tags t WHERE m.isPutaway = true AND t = :tag ORDER BY m.sortOrder ASC, m.createdAt DESC")
    Page<Manga> findByTag(@Param("tag") String tag, Pageable pageable);
    
    @Query("SELECT DISTINCT t FROM Manga m JOIN m.tags t WHERE m.isPutaway = true ORDER BY t")
    List<String> findAllTags();
}


