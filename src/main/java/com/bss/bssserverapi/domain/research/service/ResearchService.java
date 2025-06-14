package com.bss.bssserverapi.domain.research.service;

import com.bss.bssserverapi.domain.research.Research;
import com.bss.bssserverapi.domain.research.ResearchRecommend;
import com.bss.bssserverapi.domain.research.ResearchTag;
import com.bss.bssserverapi.domain.research.dto.req.CreateResearchReqDto;
import com.bss.bssserverapi.domain.research.dto.res.GetResearchPagingResDto;
import com.bss.bssserverapi.domain.research.dto.res.GetResearchPreviewResDto;
import com.bss.bssserverapi.domain.research.dto.res.GetResearchResDto;
import com.bss.bssserverapi.domain.research.repository.ResearchJpaRepository;
import com.bss.bssserverapi.domain.research.repository.ResearchRecommendJpaRepository;
import com.bss.bssserverapi.domain.research.repository.ResearchTagRepository;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ResearchService {

    private final ResearchJpaRepository researchJpaRepository;
    private final ResearchTagRepository researchTagRepository;
    private final ResearchRecommendJpaRepository researchRecommendJpaRepository;
    private final TagJpaRepository tagJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final StockRepository stockRepository;

    @Transactional
    public GetResearchResDto createResearch(final String userName, final CreateResearchReqDto createResearchReqDto){

        this.validateDateRange(createResearchReqDto.getDateStart(), createResearchReqDto.getDateEnd());
        this.validateTagList(createResearchReqDto.getTagNameList());

        User user = userJpaRepository.findByUserName(userName)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));
        Stock stock = stockRepository.findStockById(createResearchReqDto.getStockId())
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.STOCK_NOT_FOUND));

        Research research = this.createResearchEntity(createResearchReqDto, user, stock);
        List<Tag> tagList = this.createTagsAndAddToResearch(research, createResearchReqDto.getTagNameList());

        return GetResearchResDto.toDto(researchJpaRepository.save(research), tagList);
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

    private List<Tag> createTagsAndAddToResearch(final Research research, final List<String> tagNameList) {

        List<Tag> tagList = new ArrayList<>();

        for(String tagName : tagNameList) {
            Tag tag = this.getOrCreateTag(tagName);
            tagList.add(tag);
            ResearchTag researchTag = new ResearchTag();
            research.addResearchTag(researchTag);
            tag.addResearchTag(researchTag);
        }

        return tagList;
    }

    private Tag getOrCreateTag(final String tagName) {

        return tagJpaRepository.findTagByName(tagName)
                .orElseGet(() -> tagJpaRepository.save(new Tag(tagName)));
    }

    @Transactional(readOnly = true)
    public GetResearchResDto getResearch(final Long researchId){

        Research research = researchJpaRepository.findById(researchId)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.RESEARCH_NOT_FOUND));

        List<Tag> tagList = researchTagRepository.findResearchTagsByResearchId(researchId)
                .stream()
                .map(researchTag -> researchTag.getTag())
                .toList();

        return GetResearchResDto.toDto(research, tagList);
    }

    @Transactional(readOnly = true)
    public GetResearchPagingResDto getResearchPagingByStock(final Long stockId, final Long limit, final Long lastResearchId) {

        final Stock stock = stockRepository.findStockById(stockId)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.STOCK_NOT_FOUND));

        final Long fetchSize = limit + 1;

        final List<Research> researchList = (lastResearchId == 0L)
                ? researchJpaRepository.findFirstPageByStockId(stock.getId(), fetchSize)
                : researchJpaRepository.findNextPageByStockId(stock.getId(), fetchSize, lastResearchId);

        final Boolean hasNext = researchList.size() == fetchSize;
        if(hasNext){
            researchList.remove(researchList.size() - 1);
        }

        final List<GetResearchPreviewResDto> list = researchList.stream()
                .map(research -> {
                    List<Tag> tagList = research.getResearchTagList()
                            .stream()
                            .map(ResearchTag::getTag)
                            .toList();
                    return GetResearchPreviewResDto.toDto(research, tagList);
                }).toList();

        return GetResearchPagingResDto.builder()
                .getResearchPreviewResDtoList(list)
                .hasNext(hasNext)
                .build();
    }

    @Transactional(readOnly = true)
    public GetResearchPagingResDto getResearchPagingByUser(final String userName, final Long limit, final Long lastResearchId) {

        final User user = userJpaRepository.findByUserName(userName)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));

        final Long fetchSize = limit + 1;

        final List<Research> researchList = (lastResearchId == 0L)
                ? researchJpaRepository.findFirstPageByUserName(user.getUserName(), fetchSize)
                : researchJpaRepository.findNextPageByUserName(user.getUserName(), fetchSize, lastResearchId);

        final Boolean hasNext = researchList.size() == fetchSize;
        if(hasNext){
            researchList.remove(researchList.size() - 1);
        }

        final List<GetResearchPreviewResDto> list = researchList.stream()
                .map(research -> {
                    List<Tag> tagList = researchTagRepository.findResearchTagsByResearchId(research.getId())
                            .stream()
                            .map(ResearchTag::getTag)
                            .toList();
                    return GetResearchPreviewResDto.toDto(research, tagList);
                }).toList();

        return GetResearchPagingResDto.builder()
                .getResearchPreviewResDtoList(list)
                .hasNext(hasNext)
                .build();
    }

    @Transactional
    public void updateResearchRecommend(final String userName, final Long researchId) {

        final User user = userJpaRepository.findByUserName(userName)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));
        final Research research = researchJpaRepository.findById(researchId)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.RESEARCH_NOT_FOUND));

        ResearchRecommend researchRecommend = this.getOrCreateResearchRecommend(user, research);

        researchRecommend.updateRecommend();
    }

    private ResearchRecommend getOrCreateResearchRecommend(final User user, final Research research ) {

        return researchRecommendJpaRepository.findByUserIdAndResearchId(user.getId(), research.getId())
                .orElseGet(() -> researchRecommendJpaRepository.save(ResearchRecommend.builder()
                        .recommend(Boolean.FALSE)
                        .user(user)
                        .research(research)
                        .build()));
    }
}