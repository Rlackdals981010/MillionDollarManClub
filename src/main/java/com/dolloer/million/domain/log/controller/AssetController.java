package com.dolloer.million.domain.log.controller;

import com.dolloer.million.domain.log.dto.response.UserAssetResponseDto;
import com.dolloer.million.domain.log.service.AssetService;
import com.dolloer.million.response.response.ApiResponse;
import com.dolloer.million.response.response.ApiResponseAssetEnum;
import com.dolloer.million.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/asset")
public class AssetController {

    private final AssetService assetService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserAssetResponseDto>> getUserAssets(@AuthenticationPrincipal AuthUser authUser) {
        UserAssetResponseDto result = assetService.getUserAssets( authUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(result, ApiResponseAssetEnum.Asset_GET_SUCCESS.getMessage()));
    }

}