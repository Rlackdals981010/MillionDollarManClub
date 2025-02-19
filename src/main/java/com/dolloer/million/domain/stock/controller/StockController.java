package com.dolloer.million.domain.stock.controller;

import com.dolloer.million.domain.stock.dto.response.StockResponseDto;
import com.dolloer.million.domain.stock.service.StockService;
import com.dolloer.million.response.response.ApiResponse;
import com.dolloer.million.response.response.ApiResponseStockEnum;
import com.dolloer.million.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
@Slf4j
public class StockController {

    private final StockService stockService;

    // 주식 검색
    @GetMapping("/price")
    public Mono<ResponseEntity<ApiResponse<StockResponseDto>>> getStockPrice(@AuthenticationPrincipal AuthUser authUser,@RequestParam String symbol) {
        return stockService.getStockPrice(symbol)
                .map(stockResponseDto -> ResponseEntity.ok(ApiResponse.success(stockResponseDto,
                        ApiResponseStockEnum.STOCK_GET_SUCCESS.getMessage())));
    }
}
