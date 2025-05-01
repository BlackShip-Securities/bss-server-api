package com.bss.bssserverapi.fixture;

import com.bss.bssserverapi.domain.research.Research;
import com.bss.bssserverapi.domain.research.dto.req.CreateResearchReqDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ResearchFixture {

    public static final Research 리서치_생성_성공() {

        // 90일 차이
        LocalDate dateStart = LocalDate.of(2030, 1, 1);
        LocalDate dateEnd = LocalDate.of(2030, 3, 31);

        return Research.builder()
                .title("Test")
                .content("Test")
                .targetPrice(1000L)
                .dateStart(dateStart)
                .dateEnd(dateEnd)
                .build();
    }

    public static final CreateResearchReqDto 리서치_생성_성공_요청() {

        // 90일 차이
        LocalDate dateStart = LocalDate.of(2030, 1, 1);
        LocalDate dateEnd = LocalDate.of(2030, 3, 31);

        return CreateResearchReqDto.builder()
                .title("Test")
                .content("Test")
                .targetPrice(1000L)
                .dateStart(dateStart)
                .dateEnd(dateEnd)
                .stockId(1L)
                .tagNameList(List.of("Tag_0", "Tag_1", "Tag_2", "Tag_3", "Tag_4"))
                .build();
    }

    public static final CreateResearchReqDto 리서치_생성_실패_시작날짜_요청() {

        LocalDate dateStart = LocalDate.of(2030, 3, 31);
        LocalDate dateEnd = LocalDate.of(2030, 1, 1);

        return CreateResearchReqDto.builder()
                .title("Test")
                .content("Test")
                .targetPrice(1000L)
                .dateStart(dateStart)
                .dateEnd(dateEnd)
                .stockId(1L)
                .tagNameList(List.of("Tag_0", "Tag_1", "Tag_2", "Tag_3", "Tag_4"))
                .build();
    }

    public static final CreateResearchReqDto 리서치_생성_실패_날짜범위_요청() {

        LocalDate dateStart = LocalDate.of(2030, 1, 1);
        LocalDate dateEnd = LocalDate.of(2040, 3, 31);

        return CreateResearchReqDto.builder()
                .title("Test")
                .content("Test")
                .targetPrice(1000L)
                .dateStart(dateStart)
                .dateEnd(dateEnd)
                .stockId(1L)
                .tagNameList(List.of("Tag_0", "Tag_1", "Tag_2", "Tag_3", "Tag_4"))
                .build();
    }

    public static final CreateResearchReqDto 리서치_생성_실패_중복태그_요청() {

        LocalDate dateStart = LocalDate.of(2030, 1, 1);
        LocalDate dateEnd = LocalDate.of(2030, 3, 31);

        return CreateResearchReqDto.builder()
                .title("Test")
                .content("Test")
                .targetPrice(1000L)
                .dateStart(dateStart)
                .dateEnd(dateEnd)
                .stockId(1L)
                .tagNameList(List.of("Tag_0", "Tag_0", "Tag_0", "Tag_0", "Tag_0"))
                .build();
    }

    public static final CreateResearchReqDto 리서치_생성_실패_태그개수초과_요청() {

        LocalDate dateStart = LocalDate.of(2030, 1, 1);
        LocalDate dateEnd = LocalDate.of(2030, 3, 31);

        return CreateResearchReqDto.builder()
                .title("Test")
                .content("Test")
                .targetPrice(1000L)
                .dateStart(dateStart)
                .dateEnd(dateEnd)
                .stockId(1L)
                .tagNameList(List.of("Tag_0", "Tag_1", "Tag_2", "Tag_3", "Tag_4", "Tag_5"))
                .build();
    }

    public static final List<Research> 리서치_페이징_조회(final Long batchSize) {

        List<Research> researchList = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            researchList.add(Research.builder()
                    .title("Title" + "_" + i)
                    .content("Content" + "_" + i)
                    .targetPrice(1000L)
                    .dateStart(LocalDate.of(2030, 1, 1))
                    .dateEnd(LocalDate.of(2030, 3, 31))
                    .build());
        }

        return researchList;
    }
}
