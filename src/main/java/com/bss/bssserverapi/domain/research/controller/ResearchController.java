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
        throw new RuntimeException();
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(researchService.createResearch(userName, createResearchReqDto));
    }

    @GetMapping("stocks/{stock-id}/researches")
    public ResponseEntity<GetResearchPagingResDto> getResearchPagingList(
            @PathVariable("stock-id") final Long stockId,
            final Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(researchService.getResearchPagingList(stockId, pageable));
    }
}
