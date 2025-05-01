package com.bss.bssserverapi.research;

import com.bss.bssserverapi.domain.research.Research;
import com.bss.bssserverapi.domain.research.ResearchRecommend;
import com.bss.bssserverapi.domain.research.ResearchTag;
import com.bss.bssserverapi.domain.research.dto.req.CreateResearchReqDto;
import com.bss.bssserverapi.domain.research.dto.res.GetResearchPagingResDto;
import com.bss.bssserverapi.domain.research.dto.res.GetResearchResDto;
import com.bss.bssserverapi.domain.research.repository.ResearchJpaRepository;
import com.bss.bssserverapi.domain.research.repository.ResearchRecommendJpaRepository;
import com.bss.bssserverapi.domain.research.repository.ResearchTagRepository;
import com.bss.bssserverapi.domain.research.service.ResearchService;
import com.bss.bssserverapi.domain.stock.Stock;
import com.bss.bssserverapi.domain.stock.repository.StockRepository;
import com.bss.bssserverapi.domain.tag.Tag;
import com.bss.bssserverapi.domain.tag.repository.TagJpaRepository;
import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.domain.user.repository.UserJpaRepository;
import com.bss.bssserverapi.fixture.ResearchFixture;
import com.bss.bssserverapi.fixture.StockFixture;
import com.bss.bssserverapi.fixture.TagFixture;
import com.bss.bssserverapi.fixture.UserFixture;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResearchServiceTest {

    @InjectMocks
    private ResearchService researchService;

    @Mock
    private ResearchJpaRepository researchJpaRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private TagJpaRepository tagJpaRepository;

    @Mock
    private ResearchTagRepository researchTagRepository;

    @Mock
    private ResearchRecommendJpaRepository researchRecommendJpaRepository;

    @Test
    @DisplayName("리서치 생성 성공")
    void createResearch_Success() {

        // given
        CreateResearchReqDto req = ResearchFixture.리서치_생성_성공_요청();
        Research research = ResearchFixture.리서치_생성_성공();
        User user = UserFixture.사용자_0();
        Stock stock = StockFixture.주식_0();
        List<Tag> tagList = TagFixture.태그5개();
        List<String> tagNameList = req.getTagNameList();

        user.addResearch(research);
        stock.addResearch(research);

        when(userJpaRepository.findByUserName("bss_test_0")).thenReturn(Optional.of(user));
        when(researchJpaRepository.save(any(Research.class))).thenReturn(research);
        when(stockRepository.findStockById(req.getStockId())).thenReturn(Optional.of(stock));
        for(int i = 0; i < tagNameList.size(); i++){
            String tagName = tagNameList.get(i);
            Tag tag = tagList.get(i);
            when(tagJpaRepository.findTagByName(tagName)).thenReturn(Optional.empty());
            when(tagJpaRepository.save(argThat(t -> t != null && t.getName().equals(tagName)))).thenReturn(tag);
        }

        // when
        GetResearchResDto res = researchService.createResearch("bss_test_0", req);

        // then
        assertThat(res).isNotNull();
        assertThat(res.getTitle()).isEqualTo(req.getTitle());
        assertThat(res.getUserName()).isEqualTo(user.getUserName());
        assertThat(res.getTagNameList())
                .containsExactlyInAnyOrderElementsOf(tagNameList);

        verify(userJpaRepository, times(1)).findByUserName("bss_test_0");
        verify(researchJpaRepository, times(1)).save(any(Research.class));
        verify(stockRepository, times(1)).findStockById(req.getStockId());
        verify(tagJpaRepository, times(tagNameList.size())).findTagByName(anyString());
        verify(tagJpaRepository, times(tagNameList.size())).save(any(Tag.class));
    }

    @Test
    @DisplayName("리서치 생성 실패 - 시작 날짜 > 종료 날짜")
    void createResearch_Fail_StartDateAfterEndDate() {

        // given
        CreateResearchReqDto req = ResearchFixture.리서치_생성_실패_시작날짜_요청();

        // when & then
        assertThatThrownBy(() -> researchService.createResearch("bss_test_0", req))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESEARCH_INVALID_DATE_RANGE);
    }

    @Test
    @DisplayName("리서치 생성 실패 - 시작 날짜, 종료 날짜의 범위가 180일 초과")
    void createResearch_Fail_DateRangeTooLong() {

        // given
        CreateResearchReqDto req = ResearchFixture.리서치_생성_실패_날짜범위_요청();

        // when & then
        assertThatThrownBy(() -> researchService.createResearch("bss_test_0", req))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESEARCH_DATE_RANGE_TOO_LONG);
    }

    @Test
    @DisplayName("리서치 생성 실패 - 중복된 태그")
    void createResearch_Fail_DuplicatedTag() {

        // given
        CreateResearchReqDto req = ResearchFixture.리서치_생성_실패_중복태그_요청();

        // when & then
        assertThatThrownBy(() -> researchService.createResearch("bss_test_0", req))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESEARCH_TAG_DUPLICATED);
    }

    @Test
    @DisplayName("리서치 생성 실패 - 태그 개수 초과")
    void createResearch_Fail_TagLimitExceeded() {

        // given
        CreateResearchReqDto req = ResearchFixture.리서치_생성_실패_태그개수초과_요청();

        // when & then
        assertThatThrownBy(() -> researchService.createResearch("bss_test_0", req))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESEARCH_TAG_LIMIT_EXCEEDED);
    }

    @Test
    @DisplayName("리서치 단일 조회 성공")
    void getResearch_Success() {

        // given
        Research research = ResearchFixture.리서치_생성_성공();
        User user = UserFixture.사용자_0();
        List<Tag> tagList = TagFixture.태그5개();

        user.addResearch(research);

        when(researchJpaRepository.findById(research.getId())).thenReturn(Optional.of(research));
        when(researchTagRepository.findResearchTagsByResearchId(research.getId()))
                .thenReturn(tagList.stream().map(tag -> {
                    ResearchTag researchTag = new ResearchTag();
                    researchTag.setTag(tag);
                    return researchTag;
                }).toList());

        // when
        GetResearchResDto res = researchService.getResearch(research.getId());

        // then
        assertThat(res).isNotNull();
        assertThat(res.getTitle()).isEqualTo(research.getTitle());
        assertThat(res.getTagNameList()).containsExactlyInAnyOrderElementsOf(tagList.stream().map(Tag::getName).toList());
    }

    @Test
    @DisplayName("리서치 단일 조회 실패 - 리서치를 찾을 수 없음")
    void getResearch_Fail() {

        // given
        Long researchId = -1L;
        when(researchJpaRepository.findById(researchId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> researchService.getResearch(researchId))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESEARCH_NOT_FOUND);
    }

    @Test
    @DisplayName("리서치 페이징 조회 by 주식 성공 - 첫 번째 페이지 조회")
    void getResearchPagingByStock_Success_FirstPage() {

        // given
        Long stockId = 1L;
        Long limit = 10L;
        final Long BATCH_SIZE = limit + 1;

        Stock stock = StockFixture.주식_0();
        List<Research> researchList = ResearchFixture.리서치_페이징_조회(BATCH_SIZE);
        User user = UserFixture.사용자_0();
        List<Tag> tagList = TagFixture.태그5개();

        for(Research research : researchList){
            user.addResearch(research);
        }

        when(stockRepository.findStockById(stockId)).thenReturn(Optional.of(stock));
        when(researchJpaRepository.findFirstPageByStockId(stockId, BATCH_SIZE))
                .thenReturn(researchList);

        // when
        GetResearchPagingResDto res = researchService.getResearchPagingByStock(stockId, 10L, 0L);

        // then
        assertThat(res.getGetResearchPreviewResDtoList()).hasSize(10);
        assertThat(res.getHasNext()).isTrue();
    }

    @Test
    @DisplayName("리서치 페이징 조회 by 주식 성공 - N번째 페이지 조회")
    void getResearchPagingByStock_Success_NextPage() {

        // given
        Long stockId = 1L;
        Long limit = 10L;
        final Long BATCH_SIZE = limit + 1;

        Stock stock = StockFixture.주식_0();
        List<Research> researchList = ResearchFixture.리서치_페이징_조회(BATCH_SIZE - 5L);
        User user = UserFixture.사용자_0();
        List<Tag> tagList = TagFixture.태그5개();

        for(Research research : researchList){
            user.addResearch(research);
        }

        when(stockRepository.findStockById(stockId)).thenReturn(Optional.of(stock));
        when(researchJpaRepository.findNextPageByStockId(stockId, BATCH_SIZE, 5L))
                .thenReturn(researchList);

        // when
        GetResearchPagingResDto res = researchService.getResearchPagingByStock(stockId, 10L, 5L);

        // then
        assertThat(res.getGetResearchPreviewResDtoList()).hasSize(6);
        assertThat(res.getHasNext()).isFalse();
    }

//    @Test
//    @DisplayName("리서치 페이징 조회 by 사용자 성공")
//    void getResearchPagingByUser_Success() {
//
//        // given
//        Research research = ResearchFixture.리서치_생성_성공();
//        User user = UserFixture.사용자_0();
//        List<Tag> tagList = TagFixture.태그5개();
//        Pageable pageable = mock(Pageable.class);
//        String userName = user.getUserName();
//
//        user.addResearch(research);
//
//        when(userJpaRepository.findByUserName(userName)).thenReturn(Optional.of(user));
//        when(researchJpaRepository.findAllByUserId(user.getId(), pageable))
//                .thenReturn(new SliceStub<>(List.of(research), false));
//        when(researchTagRepository.findResearchTagsByResearchId(research.getId()))
//                .thenReturn(tagList.stream().map(tag -> {
//                    ResearchTag rt = new ResearchTag();
//                    rt.setTag(tag);
//                    return rt;
//                }).toList());
//
//        // when
//        GetResearchPagingResDto res = researchService.getResearchPagingByUser(userName, pageable);
//
//        // then
//        assertThat(res.getGetResearchPreviewResDtoList()).hasSize(1);
//        assertThat(res.isHasNext()).isFalse();
//    }

    @Test
    @DisplayName("리서치 페이징 조회 by 주식 실패 - 주식을 찾을 수 없음")
    void getResearchPagingByStock_Fail() {

        // given
        Long stockId = -1L;
        Pageable pageable = mock(Pageable.class);

        when(stockRepository.findStockById(stockId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> researchService.getResearchPagingByStock(stockId, 10L, 0L))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.STOCK_NOT_FOUND);
    }

    @Test
    @DisplayName("리서치 페이징 조회 by 사용자 실패 - 사용자를 찾을 수 없음")
    void getResearchPagingByUser_Fail() {

        // given
        String userName = "non_user";
        Pageable pageable = mock(Pageable.class);

        when(userJpaRepository.findByUserName(userName)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> researchService.getResearchPagingByUser(userName, 10L, 0L))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("리서치 추천 성공 - 추천하기")
    void updateResearchRecommend_Success_Recommend() {

        // given
        User user = UserFixture.사용자_0();
        Research research = ResearchFixture.리서치_생성_성공();

        when(userJpaRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(researchJpaRepository.findById(research.getId())).thenReturn(Optional.of(research));
        when(researchRecommendJpaRepository.findByUserIdAndResearchId(user.getId(), research.getId()))
                .thenReturn(Optional.empty());
        when(researchRecommendJpaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        researchService.updateResearchRecommend(user.getUserName(), research.getId());

        // then
        verify(researchRecommendJpaRepository).save(any());
    }

    @Test
    @DisplayName("리서치 추천 성공 - 추천 취소하기")
    void updateResearchRecommend_Success_UnRecommend() {

        // given
        User user = UserFixture.사용자_0();
        Research research = ResearchFixture.리서치_생성_성공();
        ResearchRecommend recommend = ResearchRecommend.builder()
                .user(user)
                .research(research)
                .recommend(true)
                .build();

        when(userJpaRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(researchJpaRepository.findById(research.getId())).thenReturn(Optional.of(research));
        when(researchRecommendJpaRepository.findByUserIdAndResearchId(user.getId(), research.getId()))
                .thenReturn(Optional.of(recommend));

        // when
        researchService.updateResearchRecommend(user.getUserName(), research.getId());

        // then
        assertThat(recommend.getRecommend()).isFalse();
    }
}