`100만불의 사나이들` 은 주식으로 자산을 관리하는 친구들을 위한 서비스 입니다.

각자 목표 금액을 100만불로 잡고, 자신의 최초 시드와 추가 시드를 관리하며 매일매일 변동되는 자산과 저축액을 입력하여 자신의 재산의 등락을 한눈에 볼 수 있게 돕습니다.

또한 미국 증시와 같은 정보를 제공하는 유튜브를 바로 접근할 수 있게 함으로 완벽하게 사용자 맞춤의 서비스를 제공합니다.

---
패치노트 0.0.1 ver
25.03.01
1. 그래프 반영을 수정했습니다.
- 기존은 A날에 todayTotal을 그대로 적용
- 변경후는 A날에 todayTotal이 없다면 그 전 total중 ㄱ가장 최신값을 적용
2. 로그인 후 1시간이 지날시 자동 로그아웃됩니다.
3. 사람마다 출력되는 그래프가 다른점을 수정했습니다
4. 로그인 한 사람이 실선으로 출력됩니다.

---
패치노트 0.0.2 ver
25.03.04
1. 그래프 호출을 위한 데이터 조회 방식 변경
   - 맴버당 수익 로그를 호출하는 것에서 한번에 전부 호출로 변경
     - 호출 쿼리가 N->1로 감소
   - Date와 Date+memberId를 통해 검색하는 것이 많아 인덱스 적용
   - 255 ms -> 130ms로 응답속도 개선