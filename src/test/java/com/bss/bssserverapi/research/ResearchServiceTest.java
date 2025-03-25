package com.bss.bssserverapi.research;

import com.bss.bssserverapi.domain.research.Research;
import com.bss.bssserverapi.domain.research.dto.CreateResearchReqDto;
import com.bss.bssserverapi.domain.research.dto.GetResearchResDto;
import com.bss.bssserverapi.domain.research.repository.ResearchJpaRepository;
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
}