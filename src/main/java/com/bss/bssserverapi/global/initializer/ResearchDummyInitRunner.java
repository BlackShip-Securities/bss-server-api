package com.bss.bssserverapi.global.initializer;

import com.bss.bssserverapi.domain.research.dto.CreateResearchReqDto;
import com.bss.bssserverapi.domain.research.repository.ResearchJpaRepository;
import com.bss.bssserverapi.domain.research.service.ResearchService;
import com.bss.bssserverapi.domain.stock.Stock;
import com.bss.bssserverapi.domain.stock.repository.StockJpaRepository;
import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.domain.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@LocalDummyInit
@Order(2)
public class ResearchDummyInitRunner implements ApplicationRunner {

    private final UserJpaRepository userJpaRepository;
    private final StockJpaRepository stockJpaRepository;
    private final ResearchJpaRepository researchJpaRepository;
    private final ResearchService researchService;

    @Override
    public void run(final ApplicationArguments args) {

        if (researchJpaRepository.count() > 0) {
            log.info("Research Dummy data exists");
            return;
        }

        this.createResearchDummy();
    }

    private void createResearchDummy() {

        List<User> userList = userJpaRepository.findAll();
        List<Stock> stockList = stockJpaRepository.findAll();

        for (User user : userList) {
            List<CreateResearchReqDto> researchList = createResearchReqDtoListByStockList(user.getUserName(), stockList);
            researchList.forEach(research -> researchService.createResearch(user.getUserName(), research));
            log.info(user.getUserName() + " research dummy saved.");
        }
        log.info("research dummy saved.");
    }

    private List<CreateResearchReqDto> createResearchReqDtoListByStockList(final String userName, final List<Stock> stockList) {

        List<CreateResearchReqDto> researchList = new ArrayList<>();
        for(Stock stock : stockList) {
            for (int i = 0; i < 5; i++) {
                CreateResearchReqDto research = new CreateResearchReqDto(
                        generateResearchName(userName, stock.getName(), i),
                        "This is the content for research.",
                        10000L,
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(10),
                        stock.getId(),
                        generateTagNameList(stock.getName(), userName, i)
                );

                researchList.add(research);
            }
        }

        return researchList;
    }

    private String generateResearchName(final String userName, final String stockName, final Integer i) {

        return "RESEARCH" + userName + "_" + stockName + "_" + i;
    }

    private List<String> generateTagNameList(final String userName, final String stockName, final Integer i) {

        List<String> tagNameList = new ArrayList<>();
        for(int j = 0; j < 5; j++) {
            tagNameList.add("TAG" + userName + "_" + stockName + "_" + i + "_" + j);
        }
        return tagNameList;
    }
}