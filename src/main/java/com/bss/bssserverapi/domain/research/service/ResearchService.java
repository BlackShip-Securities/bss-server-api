package com.bss.bssserverapi.domain.research.service;

import com.bss.bssserverapi.domain.research.Research;
import com.bss.bssserverapi.domain.research.ResearchTag;
import com.bss.bssserverapi.domain.research.dto.CreateResearchReqDto;
import com.bss.bssserverapi.domain.research.dto.GetResearchResDto;
import com.bss.bssserverapi.domain.research.repository.ResearchJpaRepository;
import com.bss.bssserverapi.domain.stock.Stock;
import com.bss.bssserverapi.domain.stock.repository.StockRepository;
import com.bss.bssserverapi.domain.tag.Tag;
import com.bss.bssserverapi.domain.tag.repository.TagJpaRepository;
import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.domain.user.repository.UserJpaRepository;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResearchService {

    private final ResearchJpaRepository researchJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final StockRepository stockRepository;
    private final TagJpaRepository tagJpaRepository;

    @Transactional
    public GetResearchResDto createResearch(final String userName, final CreateResearchReqDto createResearchReqDto){

        this.validateDateRange(createResearchReqDto.getDateStart(), createResearchReqDto.getDateEnd());
        this.validateTagList(createResearchReqDto.getTagNameList());

        User user = userJpaRepository.findByUserName(userName)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));
        Stock stock = stockRepository.findStockById(createResearchReqDto.getStockId())
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.STOCK_NOT_FOUND));

        Research research = this.createResearchEntity(createResearchReqDto, user, stock);
        this.addTagsToResearch(research, createResearchReqDto.getTagNameList());

        return GetResearchResDto.toDto(researchJpaRepository.save(research));
    }

    private void validateDateRange(final LocalDate dateStart, final LocalDate dateEnd) {

        if (dateStart.isAfter(dateEnd)) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, ErrorCode.RESEARCH_INVALID_DATE_RANGE);
        }
        if (dateStart.plusDays(100 - 1).isBefore(dateEnd)) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, ErrorCode.RESEARCH_DATE_RANGE_TOO_LONG);
        }
    }

    private void validateTagList(final List<String> tagList){

        Set<String> uniqueTags = new HashSet<>(tagList);

        if (uniqueTags.size() > 5) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, ErrorCode.RESEARCH_TAG_LIMIT_EXCEEDED);
        }
        if (uniqueTags.size() != tagList.size()) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, ErrorCode.RESEARCH_TAG_DUPLICATED);
        }
    }

    private Research createResearchEntity(final CreateResearchReqDto createResearchReqDto, final User user, final Stock stock) {

        Research research = Research.toEntity(createResearchReqDto);
        user.addResearch(research);
        stock.addResearch(research);
        return research;
    }

    private void addTagsToResearch(final Research research, final List<String> tagNameList) {

        for(String tagName : tagNameList) {
            Tag tag = this.getOrCreateTag(tagName);
            ResearchTag researchTag = new ResearchTag();
            research.addResearchTag(researchTag);
            tag.addResearchTag(researchTag);
        }
    }

    private Tag getOrCreateTag(final String tagName) {

        return tagJpaRepository.findTagByName(tagName)
                .orElseGet(() -> tagJpaRepository.save(new Tag(tagName)));
    }
}