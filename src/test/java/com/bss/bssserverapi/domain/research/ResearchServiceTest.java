//package com.bss.bssserverapi.domain.research;
//
//import com.bss.bssserverapi.domain.research.dto.CreateResearchReqDto;
//import com.bss.bssserverapi.domain.research.dto.GetResearchResDto;
//import com.bss.bssserverapi.domain.research.repository.ResearchJpaRepository;
//import com.bss.bssserverapi.domain.research.service.ResearchService;
//import com.bss.bssserverapi.domain.user.User;
//import com.bss.bssserverapi.domain.user.repository.UserJpaRepository;
//import com.bss.bssserverapi.global.exception.ErrorCode;
//import com.bss.bssserverapi.global.exception.GlobalException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//
//import java.time.LocalDate;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ResearchServiceTest {
//
//    @InjectMocks
//    private ResearchService researchService;
//
//    @Mock
//    private ResearchJpaRepository researchJpaRepository;
//
//    @Mock
//    private UserJpaRepository userJpaRepository;
//
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        user = User.builder()
//                .userName("bss_test")
//                .password("Qq12341234@")
//                .build();
//    }
//
//    @Test
//    @DisplayName("리서치 생성 성공")
//    void createResearch_Success() {
//
//        // given
//        LocalDate dateStart = LocalDate.of(2025, 1, 1);
//        LocalDate dateEnd = LocalDate.of(2025, 3, 31); // 90일 차이
//
//        CreateResearchReqDto req = CreateResearchReqDto.builder()
//                .title("Test")
//                .content("Test")
//                .targetPrice(1000L)
//                .dateStart(dateStart)
//                .dateEnd(dateEnd)
//                .build();
//
//        Research research = Research.toEntity(req);
//        user.addResearch(research);
//
//        when(userJpaRepository.findByUserName("bss_test")).thenReturn(Optional.of(user));
//        when(researchJpaRepository.save(any(Research.class))).thenReturn(research);
//
//        // when
//        GetResearchResDto result = researchService.createResearch("bss_test", req);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.getTitle()).isEqualTo("Test");
//        assertThat(result.getUserName()).isEqualTo("bss_test");
//
//        verify(userJpaRepository, times(1)).findByUserName("bss_test");
//        verify(researchJpaRepository, times(1)).save(any(Research.class));
//    }
//
//    @Test
//    @DisplayName("리서치 생성 실패 - 시작 날짜 > 종료 날짜")
//    void createResearch_Fail_StartDateAfterEndDate() {
//
//        // given
//        LocalDate dateStart = LocalDate.of(2025, 3, 31);
//        LocalDate dateEnd = LocalDate.of(2025, 1, 1);
//
//        CreateResearchReqDto req = CreateResearchReqDto.builder()
//                .title("Test")
//                .content("Test")
//                .targetPrice(1000L)
//                .dateStart(dateStart)
//                .dateEnd(dateEnd)
//                .build();
//
//        when(userJpaRepository.findByUserName("bss_test")).thenReturn(Optional.of(user));
//
//        // when & then
//        assertThatThrownBy(() -> researchService.createResearch("bss_test", req))
//                .isInstanceOf(GlobalException.class)
//                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
//                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESEARCH_INVALID_DATE_RANGE);
//
//        verify(userJpaRepository, times(1)).findByUserName("bss_test");
//        verify(researchJpaRepository, never()).save(any(Research.class));
//    }
//
//    @Test
//    @DisplayName("리서치 생성 실패 - 시작 날짜, 종료 날짜의 범위가 180일 초과")
//    void createResearch_Fail_DateRangeTooLong() {
//
//        // given
//        LocalDate dateStart = LocalDate.of(2025, 1, 1);
//        LocalDate dateEnd = LocalDate.of(2025, 7, 1);
//
//        CreateResearchReqDto req = CreateResearchReqDto.builder()
//                .title("Test")
//                .content("Test")
//                .targetPrice(1000L)
//                .dateStart(dateStart)
//                .dateEnd(dateEnd)
//                .build();
//
//        when(userJpaRepository.findByUserName("bss_test")).thenReturn(Optional.of(user));
//
//        // when & then
//        assertThatThrownBy(() -> researchService.createResearch("bss_test", req))
//                .isInstanceOf(GlobalException.class)
//                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
//                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESEARCH_DATE_RANGE_TOO_LONG);
//
//        verify(userJpaRepository, times(1)).findByUserName("bss_test");
//        verify(researchJpaRepository, never()).save(any(Research.class));
//    }
//}