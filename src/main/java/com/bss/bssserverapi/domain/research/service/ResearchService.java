package com.bss.bssserverapi.domain.research.service;

import com.bss.bssserverapi.domain.research.Research;
import com.bss.bssserverapi.domain.research.dto.CreateResearchReqDto;
import com.bss.bssserverapi.domain.research.dto.GetResearchResDto;
import com.bss.bssserverapi.domain.research.repository.ResearchJpaRepository;
import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.domain.user.repository.UserJpaRepository;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResearchService {

    private final ResearchJpaRepository researchJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Transactional
    public GetResearchResDto createResearch(final String userName, final CreateResearchReqDto createResearchReqDto){

        User user = userJpaRepository.findByUserName(userName)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));

        validateDateRange(createResearchReqDto.getDateStart(), createResearchReqDto.getDateEnd());

        Research research = Research.toEntity(createResearchReqDto);

        user.addResearch(research);

        return GetResearchResDto.toDto(researchJpaRepository.save(research));
    }

    private void validateDateRange(LocalDate dateStart, LocalDate dateEnd) {

        if (dateStart.isAfter(dateEnd)) {

            throw new GlobalException(HttpStatus.BAD_REQUEST, ErrorCode.RESEARCH_INVALID_DATE_RANGE);
        }

        if (dateStart.plusDays(100 - 1).isBefore(dateEnd)) {

            throw new GlobalException(HttpStatus.BAD_REQUEST, ErrorCode.RESEARCH_DATE_RANGE_TOO_LONG);
        }
    }
}
