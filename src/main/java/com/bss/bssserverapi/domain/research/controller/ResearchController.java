package com.bss.bssserverapi.domain.research.controller;

import com.bss.bssserverapi.domain.research.dto.CreateResearchReqDto;
import com.bss.bssserverapi.domain.research.dto.GetResearchPagingResDto;
import com.bss.bssserverapi.domain.research.dto.GetResearchResDto;
import com.bss.bssserverapi.domain.research.service.ResearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
            final Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(researchService.getResearchPagingByStock(stockId, pageable));
    }

    @GetMapping("/users/{userName}/researches")
    public ResponseEntity<GetResearchPagingResDto> getResearchPagingByUser(
            @PathVariable("userName") final String userName,
            final Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(researchService.getResearchPagingByUser(userName, pageable));
    }
}
