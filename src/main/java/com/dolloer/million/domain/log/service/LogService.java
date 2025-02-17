package com.dolloer.million.domain.seed.service;


import com.dolloer.million.domain.member.entity.Member;
import com.dolloer.million.domain.member.repository.MemberRepository;
import com.dolloer.million.domain.seed.dto.response.SeedResponseDto;
import com.dolloer.million.domain.seed.entity.SeedHistory;
import com.dolloer.million.domain.seed.repository.SeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeedService {

    private final MemberRepository memberRepository;
    private final SeedRepository seedRepository;

    // 시드 설정
    @Transactional
    public void setSeedMoney(Long memberId, Double seedMoney) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("등록하고오셈"));
        try{
            if (member.getSeedMoney() == 0) {
                member.initSeed(seedMoney);
            } else {
                member.changeSeed(seedMoney);
            }
            SeedHistory seedHistory = new SeedHistory(member, seedMoney, member.getSeedMoney());
            seedRepository.save(seedHistory);

            memberRepository.saveAndFlush(member);
        }catch (ObjectOptimisticLockingFailureException e){
            log.info("Method: setSeedMoney, Version:{}, ID:{}, seedMoney:{} 동시성 문제 발생", member.getVersion(), memberId, seedMoney);
        }
    }

    // 시드 로그 반환
    @Transactional(readOnly = true)
    public Page<SeedResponseDto> getSeedHistory(Long memberId, Pageable pageable) {
        Page<SeedHistory> seedHistories = seedRepository.findByMemberId(memberId, pageable);
        return seedHistories.map(SeedResponseDto::from);
    }

}
