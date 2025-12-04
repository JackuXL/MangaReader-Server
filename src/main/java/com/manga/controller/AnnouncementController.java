package com.manga.controller;

import com.manga.dto.AnnouncementResponse;
import com.manga.dto.ApiResponse;
import com.manga.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@Tag(name = "Announcement", description = "Announcement endpoints")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    @Operation(summary = "Get all announcements")
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> getAnnouncements() {
        List<AnnouncementResponse> announcements = announcementService.getAnnouncements();
        return ResponseEntity.ok(ApiResponse.success(announcements));
    }
}

