package com.management.library.service.query;

import com.management.library.service.member.MemberService;
import com.management.library.service.member.dto.MemberServiceReadDto;
import com.management.library.service.query.dto.MemberTotalInfoDto;
import com.management.library.service.rental.RentalRedisService;
import com.management.library.service.request.RedisRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberTotalInfoService {

  private final RedisRequestService redisRequestService;
  private final RentalRedisService rentalRedisService;
  private final MemberService memberService;

  public MemberTotalInfoDto getMemberTotalInfo(String memberCode) {
    MemberServiceReadDto memberData = memberService.getMemberData(memberCode);
    String managementRequestCount = redisRequestService.getManagementRequestCount(memberCode);
    String newBookRequestCount = redisRequestService.getNewBookRequestCount(memberCode);
    String memberRemainRentalCount = rentalRedisService.getMemberRemainRentalCount(memberCode);
    String memberRentalStatus = "available";

    if (rentalRedisService.checkMemberRentalPenalty(memberCode) ||
        memberRemainRentalCount.equals("0")) {
      memberRentalStatus = "unavailable";
    }

    return MemberTotalInfoDto.of(memberData, managementRequestCount, newBookRequestCount,
        memberRemainRentalCount, memberRentalStatus);
  }
}
