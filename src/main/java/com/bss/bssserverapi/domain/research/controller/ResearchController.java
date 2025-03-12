package com.bss.bssserverapi.domain.research.controller;

import com.bss.bssserverapi.domain.research.dto.CreateResearchReqDto;
import com.bss.bssserverapi.domain.research.dto.GetResearchResDto;
import com.bss.bssserverapi.domain.research.service.ResearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/researches")
public class ResearchController {

    private final ResearchService researchService;

    @PostMapping
    public ResponseEntity<GetResearchResDto> createResearch(
            @AuthenticationPrincipal String userName,
            @RequestBody @Valid final CreateResearchReqDto createResearchReqDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(researchService.createResearch(userName, createResearchReqDto));
    }
}
