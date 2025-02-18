package com.dolloer.million.domain.money.controller;

import com.dolloer.million.domain.money.dto.request.QuestRequestDto;
import com.dolloer.million.domain.money.dto.response.UpcomingQuestResponseDto;
import com.dolloer.million.domain.money.service.MoneyService;
import com.dolloer.million.response.response.ApiResponse;
import com.dolloer.million.response.response.ApiResponseMoneyEnum;
import com.dolloer.million.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/money")
@Slf4j
public class MoneyController {

    private final MoneyService moneyService;

    // 처리할 일퀘
    @PostMapping("/upcoming")
    public ResponseEntity<ApiResponse<UpcomingQuestResponseDto>> upcomingQuest(@AuthenticationPrincipal AuthUser authUser, @RequestBody QuestRequestDto questRequestDto){
        log.info("호출됨");
        UpcomingQuestResponseDto ret = moneyService.upcomingQuest(authUser.getUserId(),questRequestDto.getPer());
        return ResponseEntity.ok(ApiResponse.success(ret, ApiResponseMoneyEnum.QUEST_UP_SUCCESS.getMessage()));
    }

}
