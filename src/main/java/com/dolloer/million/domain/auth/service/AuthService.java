package com.dolloer.million.domain.auth.service;

import com.dolloer.million.domain.auth.dto.response.SignInResponseDto;
import com.dolloer.million.domain.auth.dto.response.SignUpResponseDto;
import com.dolloer.million.domain.member.entity.Member;
import com.dolloer.million.domain.member.repository.MemberRepository;
import com.dolloer.million.response.exception.CustomException;
import com.dolloer.million.response.response.ApiResponseAuthEnum;
import com.dolloer.million.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    // 회원 등록
    @Transactional
    public SignUpResponseDto signUp(String name){
        memberRepository.findByName(name)
                .ifPresent(member -> {
                    throw new CustomException(ApiResponseAuthEnum.MEMBER_ALREADY_EXIST);
                });

        Member member = new Member(name);
        memberRepository.save(member);
        return new SignUpResponseDto(member.getName());
    }

    // 로그인
    public SignInResponseDto signIn(String name){
        Member member = memberRepository.findByName(name)
                .orElseThrow(() -> new CustomException(ApiResponseAuthEnum.MEMBER_NO_EXIST));

        return new SignInResponseDto(jwtUtil.createToken(member.getId(), member.getName()));
    }


}
