package com.bss.bssserverapi.domain.research.controller;

import com.bss.bssserverapi.domain.research.dto.req.CreateResearchReqDto;
import com.bss.bssserverapi.domain.research.dto.res.GetResearchPagingResDto;
import com.bss.bssserverapi.domain.research.dto.res.GetResearchResDto;
import com.bss.bssserverapi.domain.research.service.ResearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ResearchController {

    private final ResearchService researchService;

    @PostMapping("/researches")
    public ResponseEntity<GetResearchResDto> createResearch(
            @AuthenticationPrincipal final String userName,
            @RequestBody @Valid final CreateResearchReqDto createResearchReqDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(researchService.createResearch(userName, createResearchReqDto));
    }

    @GetMapping("/researches/{researchId}")
    public ResponseEntity<GetResearchResDto> getResearch(@PathVariable("researchId") final Long researchId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(researchService.getResearch(researchId));
    }

    @GetMapping("/stocks/{stockId}/researches")
    public ResponseEntity<GetResearchPagingResDto> getResearchPagingByStock(
            @PathVariable("stockId") final Long stockId,
            @RequestParam(value = "lastResearchId", defaultValue = "0") final Long lastResearchId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(researchService.getResearchPagingByStock(stockId, 10L, lastResearchId));
    }

    @GetMapping("/users/{userName}/researches")
    public ResponseEntity<GetResearchPagingResDto> getResearchPagingByUser(
            @PathVariable("userName") final String userName,
            @RequestParam(value = "lastResearchId", defaultValue = "0") final Long lastResearchId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(researchService.getResearchPagingByUser(userName, 10L, lastResearchId));
    }

    @PatchMapping("/researches/{researchId}/recommend")
    public ResponseEntity<?> updateResearchRecommend(
            @AuthenticationPrincipal final String userName,
            @PathVariable("researchId") final Long researchId) {

        researchService.updateResearchRecommend(userName, researchId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
