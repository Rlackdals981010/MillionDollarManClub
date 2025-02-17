package com.dolloer.million.domain.seed.controller;

import com.dolloer.million.domain.seed.dto.request.SeedRequestDto;
import com.dolloer.million.domain.seed.dto.response.SeedResponseDto;
import com.dolloer.million.domain.seed.service.SeedService;
import com.dolloer.million.response.response.ApiResponse;
import com.dolloer.million.response.response.ApiResponseSeedEnum;
import com.dolloer.million.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seed")
@Slf4j
public class SeedController {

    private final SeedService seedService;

    // 시드 설정 / 변경
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> setSeedMoney(@AuthenticationPrincipal AuthUser authUser, @RequestBody SeedRequestDto seedMoneyRequestDto) {
        seedService.setSeedMoney(authUser.getUserId(), seedMoneyRequestDto.getSeedMoney());
        return ResponseEntity.ok(ApiResponse.success(ApiResponseSeedEnum.SEED_SET_SUCCESS.getMessage()));
    }

    // 시드 로그 조회
    @GetMapping
    public ResponseEntity<Page<SeedResponseDto>> getSeedHistory(
            @RequestParam Long memberId,
            @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SeedResponseDto> result = seedService.getSeedHistory(memberId, pageable);
        return ResponseEntity.ok(result);
    }


}
