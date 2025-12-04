package com.manga.service;

import com.manga.dto.MangaImportRequest;
import com.manga.dto.MangaResponse;
import com.manga.entity.Chapter;
import com.manga.entity.Manga;
import com.manga.repository.MangaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MangaService {

    private final MangaRepository mangaRepository;
    private final CdnService cdnService;

    // Public methods that apply CDN transformation AFTER retrieving from cache
    
    public Page<MangaResponse> getAllManga(Pageable pageable) {
        return getAllMangaCached(pageable).map(this::applyCdnTransformation);
    }

    public Page<MangaResponse> getLatestManga(Pageable pageable) {
        return getLatestMangaCached(pageable).map(this::applyCdnTransformation);
    }

    public Page<MangaResponse> getPopularManga(Pageable pageable) {
        return getPopularMangaCached(pageable).map(this::applyCdnTransformation);
    }

    public Page<MangaResponse> getChoicenessManga(Pageable pageable) {
        return getChoicenessMangaCached(pageable).map(this::applyCdnTransformation);
    }

    public Page<MangaResponse> getRecommendedManga(Pageable pageable) {
        return getRecommendedMangaCached(pageable).map(this::applyCdnTransformation);
    }

    public Page<MangaResponse> getNewManga(Pageable pageable) {
        return getNewMangaCached(pageable).map(this::applyCdnTransformation);
    }

    public Page<MangaResponse> getMangaByCountry(String country, Pageable pageable) {
        return getMangaByCountryCached(country, pageable).map(this::applyCdnTransformation);
    }

    public Page<MangaResponse> getMangaByTendency(String tendency, Pageable pageable) {
        return getMangaByTendencyCached(tendency, pageable).map(this::applyCdnTransformation);
    }

    public List<MangaResponse> getRelatedWorks(Long mangaId, int limit) {
        return getRelatedWorksCached(mangaId, limit).stream()
                .map(this::applyCdnTransformation)
                .collect(Collectors.toList());
    }

    public MangaResponse getMangaById(Long id) {
        return applyCdnTransformation(getMangaByIdCached(id));
    }

    public Page<MangaResponse> getMangaByTag(String tag, Pageable pageable) {
        return getMangaByTagCached(tag, pageable).map(this::applyCdnTransformation);
    }

    @Cacheable(value = "tags", key = "'allTags'")
    public com.manga.dto.TagsResponse getAllTags() {
        return com.manga.dto.TagsResponse.of(mangaRepository.findAllTags());
    }

    // Private cacheable methods that store RELATIVE paths only
    
    @Cacheable(value = "mangaList", key = "'all_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    private Page<MangaResponse> getAllMangaCached(Pageable pageable) {
        return mangaRepository.findAllPutaway(pageable)
                .map(MangaResponse::fromEntity);
    }

    @Cacheable(value = "mangaList", key = "'latest_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    private Page<MangaResponse> getLatestMangaCached(Pageable pageable) {
        return mangaRepository.findLatest(pageable)
                .map(MangaResponse::fromEntity);
    }

    @Cacheable(value = "featured", key = "'popular_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    private Page<MangaResponse> getPopularMangaCached(Pageable pageable) {
        return mangaRepository.findTopByViewCount(pageable)
                .map(MangaResponse::fromEntity);
    }

    @Cacheable(value = "featured", key = "'choiceness_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    private Page<MangaResponse> getChoicenessMangaCached(Pageable pageable) {
        return mangaRepository.findChoiceness(pageable)
                .map(MangaResponse::fromEntity);
    }

    @Cacheable(value = "recommended", key = "'recommended_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    private Page<MangaResponse> getRecommendedMangaCached(Pageable pageable) {
        return mangaRepository.findRecommended(pageable)
                .map(MangaResponse::fromEntity);
    }

    @Cacheable(value = "mangaList", key = "'new_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    private Page<MangaResponse> getNewMangaCached(Pageable pageable) {
        return mangaRepository.findNew(pageable)
                .map(MangaResponse::fromEntity);
    }

    @Cacheable(value = "mangaList", key = "'country_' + #country + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    private Page<MangaResponse> getMangaByCountryCached(String country, Pageable pageable) {
        return mangaRepository.findByCountry(country, pageable)
                .map(MangaResponse::fromEntity);
    }

    @Cacheable(value = "mangaList", key = "'tendency_' + #tendency + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    private Page<MangaResponse> getMangaByTendencyCached(String tendency, Pageable pageable) {
        return mangaRepository.findByTendency(tendency, pageable)
                .map(MangaResponse::fromEntity);
    }

    @Cacheable(value = "mangaList", key = "'tag_' + #tag + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    private Page<MangaResponse> getMangaByTagCached(String tag, Pageable pageable) {
        return mangaRepository.findByTag(tag, pageable)
                .map(MangaResponse::fromEntity);
    }

    @Cacheable(value = "recommended", key = "'related_' + #mangaId + '_' + #limit")
    private List<MangaResponse> getRelatedWorksCached(Long mangaId, int limit) {
        List<Manga> randomManga = mangaRepository.findRandomManga(mangaId, limit);
        return randomManga.stream()
                .map(MangaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "mangaDetail", key = "#id")
    private MangaResponse getMangaByIdCached(Long id) {
        Manga manga = mangaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manga not found"));
        return MangaResponse.fromEntity(manga);
    }

    @Transactional
    public void incrementViewCount(Long id) {
        Manga manga = mangaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manga not found"));
        manga.setViewCount(manga.getViewCount() + 1);
        mangaRepository.save(manga);
    }

    /**
     * 搜索漫画
     * 
     * @param keyword 关键词
     * @param tag 标签筛选（可选）
     * @param sort 排序方式：default（默认）、updated（最近更新）、created（最近添加）
     * @param pageable 分页参数
     * @return 搜索结果
     */
    public Page<MangaResponse> searchManga(String keyword, String tag, String sort, Pageable pageable) {
        return searchMangaCached(keyword, tag, sort, pageable).map(this::applyCdnTransformation);
    }

    @Cacheable(value = "mangaSearch", key = "#keyword + '_' + (#tag ?: 'all') + '_' + (#sort ?: 'default') + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    private Page<MangaResponse> searchMangaCached(String keyword, String tag, String sort, Pageable pageable) {
        // 根据排序方式创建新的 Pageable
        org.springframework.data.domain.Sort sortOrder = switch (sort != null ? sort : "default") {
            case "updated" -> org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "updatedAt");
            case "created" -> org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt");
            default -> org.springframework.data.domain.Sort.by(
                    org.springframework.data.domain.Sort.Order.asc("sortOrder"),
                    org.springframework.data.domain.Sort.Order.desc("createdAt")
            );
        };
        
        Pageable sortedPageable = org.springframework.data.domain.PageRequest.of(
                pageable.getPageNumber(), 
                pageable.getPageSize(), 
                sortOrder
        );
        
        if (tag != null && !tag.isEmpty()) {
            return mangaRepository.searchByKeywordAndTag(keyword, tag, sortedPageable)
                    .map(MangaResponse::fromEntity);
        }
        return mangaRepository.searchByKeyword(keyword, sortedPageable)
                .map(MangaResponse::fromEntity);
    }

    /**
     * Apply CDN transformation to MangaResponse after cache retrieval
     * This ensures cached data contains relative paths and CDN URLs are built dynamically
     * 
     * @param response MangaResponse with relative paths
     * @return MangaResponse with full CDN URLs
     */
    private MangaResponse applyCdnTransformation(MangaResponse response) {
        response.setCoverImageUrl(cdnService.buildUrl(response.getCoverImageUrl()));
        response.setDetailImages(cdnService.buildUrls(response.getDetailImages()));
        return response;
    }

    @Transactional
    @CacheEvict(value = {"mangaSearch", "mangaList", "featured", "recommended", "mangaDetail", "tags"}, allEntries = true)
    public MangaResponse importManga(MangaImportRequest request) {
        Manga manga = new Manga();
        manga.setTitle(request.getTitle());
        manga.setOldName(request.getOldName());
        manga.setDescription(request.getDescription());
        
        // 提取相对路径保存到数据库（支持完整 URL 或相对路径输入）
        manga.setCoverImageUrl(cdnService.extractRelativePath(request.getCoverImageUrl()));
        
        if (request.getDetailImages() != null) {
            manga.setDetailImages(cdnService.extractRelativePaths(request.getDetailImages()));
        }
        
        manga.setAuthor(request.getAuthor());
        manga.setSlogan(request.getSlogan());
        
        manga.setIsChoiceness(request.getIsChoiceness() != null ? request.getIsChoiceness() : false);
        manga.setIsRecommend(request.getIsRecommend() != null ? request.getIsRecommend() : false);
        manga.setIsNew(request.getIsNew() != null ? request.getIsNew() : false);
        
        manga.setPeriod(request.getPeriod());
        manga.setIsPutaway(request.getIsPutaway() != null ? request.getIsPutaway() : true);
        
        if (request.getPutawayTime() != null && !request.getPutawayTime().isEmpty()) {
            manga.setPutawayTime(java.time.LocalDateTime.parse(request.getPutawayTime()));
        }
        
        if (request.getOnlineTime() != null && !request.getOnlineTime().isEmpty()) {
            manga.setOnlineTime(java.time.LocalDateTime.parse(request.getOnlineTime()));
        }
        
        manga.setTendency(request.getTendency());
        manga.setCountry(request.getCountry());
        manga.setIsFinish(request.getIsFinish());
        manga.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        
        if (request.getTags() != null) {
            manga.setTags(request.getTags());
        }
        
        if (request.getLabels() != null) {
            manga.setLabels(request.getLabels());
        }
        
        manga.setSource(request.getSource());
        
        manga.setViewCount(0);
        manga.setFavoriteCount(0);

        List<Chapter> chapters = new ArrayList<>();
        if (request.getChapters() != null) {
            for (MangaImportRequest.ChapterImportRequest chapterReq : request.getChapters()) {
                Chapter chapter = new Chapter();
                chapter.setManga(manga);
                chapter.setTitle(chapterReq.getTitle());
                chapter.setChapterNumber(chapterReq.getChapterNumber());
                // 提取相对路径保存
                chapter.setPageUrls(cdnService.extractRelativePaths(chapterReq.getPageUrls()));
                chapter.setViewCount(0);
                chapters.add(chapter);
            }
        }
        manga.setChapters(chapters);

        manga = mangaRepository.save(manga);
        // 返回时构建完整 CDN URL（非缓存方法，直接使用 cdnService）
        return applyCdnTransformation(MangaResponse.fromEntity(manga));
    }

    @Transactional
    @CacheEvict(value = {"mangaSearch", "mangaList", "featured", "recommended", "mangaDetail", "tags"}, allEntries = true)
    public List<MangaResponse> batchImportManga(List<MangaImportRequest> requests) {
        List<MangaResponse> responses = new ArrayList<>();
        for (MangaImportRequest request : requests) {
            responses.add(importManga(request));
        }
        return responses;
    }

    @Transactional
    @CacheEvict(value = {"mangaSearch", "mangaList", "featured", "recommended", "mangaDetail", "tags"}, allEntries = true)
    public void deleteManga(Long id) {
        if (!mangaRepository.existsById(id)) {
            throw new RuntimeException("Manga not found");
        }
        mangaRepository.deleteById(id);
    }

    @Transactional
    public void incrementFavoriteCount(Long id) {
        Manga manga = mangaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manga not found"));
        manga.setFavoriteCount(manga.getFavoriteCount() + 1);
        mangaRepository.save(manga);
    }

    @Transactional
    public void decrementFavoriteCount(Long id) {
        Manga manga = mangaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manga not found"));
        manga.setFavoriteCount(Math.max(0, manga.getFavoriteCount() - 1));
        mangaRepository.save(manga);
    }
}


