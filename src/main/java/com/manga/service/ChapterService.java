package com.manga.service;

import com.manga.dto.ChapterCreateRequest;
import com.manga.dto.ChapterResponse;
import com.manga.entity.Chapter;
import com.manga.entity.Manga;
import com.manga.repository.ChapterRepository;
import com.manga.repository.MangaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final MangaRepository mangaRepository;
    private final CdnService cdnService;

    // Public methods that apply CDN transformation AFTER retrieving from cache
    
    public List<ChapterResponse> getChaptersByMangaId(Long mangaId) {
        return getChaptersByMangaIdCached(mangaId).stream()
                .map(this::applyCdnTransformation)
                .collect(Collectors.toList());
    }

    public ChapterResponse getChapterById(Long id) {
        return applyCdnTransformation(getChapterByIdCached(id));
    }

    public ChapterResponse getChapterByMangaIdAndNumber(Long mangaId, Integer chapterNumber) {
        return applyCdnTransformation(getChapterByMangaIdAndNumberCached(mangaId, chapterNumber));
    }

    // Private cacheable methods that store RELATIVE paths only
    
    @Cacheable(value = "chapterList", key = "#mangaId")
    private List<ChapterResponse> getChaptersByMangaIdCached(Long mangaId) {
        return chapterRepository.findByMangaIdOrderByChapterNumberAsc(mangaId)
                .stream()
                .map(ChapterResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "chapterList", key = "'chapter_' + #id")
    private ChapterResponse getChapterByIdCached(Long id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        return ChapterResponse.fromEntity(chapter);
    }

    @Cacheable(value = "chapterList", key = "'manga_' + #mangaId + '_ch_' + #chapterNumber")
    private ChapterResponse getChapterByMangaIdAndNumberCached(Long mangaId, Integer chapterNumber) {
        Chapter chapter = chapterRepository.findByMangaIdAndChapterNumber(mangaId, chapterNumber)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        return ChapterResponse.fromEntity(chapter);
    }

    @Transactional
    public void incrementViewCount(Long id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        chapter.setViewCount(chapter.getViewCount() + 1);
        chapterRepository.save(chapter);
    }

    /**
     * 新增章节
     * 
     * @param request 章节信息
     * @return 创建的章节
     */
    @Transactional
    @CacheEvict(value = {"chapterList", "mangaDetail"}, allEntries = true)
    public ChapterResponse createChapter(ChapterCreateRequest request) {
        // 验证漫画是否存在
        Manga manga = mangaRepository.findById(request.getMangaId())
                .orElseThrow(() -> new RuntimeException("Manga not found with id: " + request.getMangaId()));

        // 检查章节编号是否已存在
        chapterRepository.findByMangaIdAndChapterNumber(request.getMangaId(), request.getChapterNumber())
                .ifPresent(existing -> {
                    throw new RuntimeException("Chapter number " + request.getChapterNumber() 
                            + " already exists for manga id: " + request.getMangaId());
                });

        // 创建章节
        Chapter chapter = new Chapter();
        chapter.setManga(manga);
        chapter.setTitle(request.getTitle());
        chapter.setChapterNumber(request.getChapterNumber());
        
        // 提取相对路径存储（支持传入完整 URL 或相对路径）
        List<String> relativePaths = cdnService.extractRelativePaths(request.getPageUrls());
        chapter.setPageUrls(relativePaths);
        
        chapter.setViewCount(0);

        // 保存章节
        Chapter savedChapter = chapterRepository.save(chapter);

        // 返回时构建完整 CDN URL（非缓存方法，直接应用转换）
        return applyCdnTransformation(ChapterResponse.fromEntity(savedChapter));
    }

    /**
     * 删除章节
     * 
     * @param id 章节ID
     */
    @Transactional
    @CacheEvict(value = {"chapterList", "mangaDetail"}, allEntries = true)
    public void deleteChapter(Long id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found with id: " + id));
        chapterRepository.delete(chapter);
    }

    /**
     * Apply CDN transformation to ChapterResponse after cache retrieval
     * This ensures cached data contains relative paths and CDN URLs are built dynamically
     * 
     * @param response ChapterResponse with relative paths
     * @return ChapterResponse with full CDN URLs
     */
    private ChapterResponse applyCdnTransformation(ChapterResponse response) {
        response.setPageUrls(cdnService.buildUrls(response.getPageUrls()));
        return response;
    }
}


