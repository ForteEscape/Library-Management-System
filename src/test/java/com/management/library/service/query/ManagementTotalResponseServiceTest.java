package com.management.library.service.query;

import static com.management.library.domain.type.RequestStatus.ACCEPTED;
import static com.management.library.domain.type.RequestStatus.AWAIT;
import static org.assertj.core.api.Assertions.assertThat;

import com.management.library.AbstractContainerBaseTest;
import com.management.library.domain.type.RequestStatus;
import com.management.library.repository.admin.AdministratorRepository;
import com.management.library.repository.management.ManagementRequestRepository;
import com.management.library.repository.management.ManagementRequestResultRepository;
import com.management.library.repository.member.MemberRepository;
import com.management.library.service.admin.AdminService;
import com.management.library.service.admin.dto.AdminCreateServiceDto;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.dto.MemberCreateServiceDto;
import com.management.library.service.member.dto.MemberCreateServiceDto.Response;
import com.management.library.service.query.dto.ManagementResultDto;
import com.management.library.service.query.dto.ManagementTotalResponseDto;
import com.management.library.service.request.RedisRequestService;
import com.management.library.service.request.management.ManagementService;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto;
import com.management.library.service.result.management.ManagementResultService;
import com.management.library.service.result.management.dto.ManagementResultCreateDto;
import com.management.library.service.result.management.dto.ManagementResultCreateDto.Request;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ManagementTotalResponseServiceTest extends AbstractContainerBaseTest {

  @Autowired
  private ManagementService managementService;
  @Autowired
  private ManagementResultService managementResultService;
  @Autowired
  private RedisRequestService redisRequestService;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private AdministratorRepository administratorRepository;
  @Autowired
  private ManagementRequestRepository managementRequestRepository;
  @Autowired
  private ManagementRequestResultRepository managementRequestResultRepository;
  @Autowired
  private MemberService memberService;
  @Autowired
  private AdminService adminService;
  @Autowired
  private ManagementTotalResponseService managementTotalResponseService;
  private static final String MANAGEMENT_REQUEST_PREFIX = "management-request-id:";
  private static final String KEY = "management-request-count:";

  @AfterEach
  void tearDown() {
    managementRequestResultRepository.deleteAllInBatch();
    managementRequestRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
    administratorRepository.deleteAllInBatch();

    redisRequestService.deleteCache(KEY);

    for (int i = 1; i < 100; i++) {
      redisRequestService.deleteCache(MANAGEMENT_REQUEST_PREFIX + i);
    }
  }

  @DisplayName("운영 개선 요청에 대한 내역과 그 답안까지 모두 가져올 수 있다.")
  @Test
  public void createTotalResponse() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest = createMemberRequest("kim", "980101", "경남", "김해",
        "삼계로");
    Response savedMember = memberService.createMember(memberRequest);

    AdminCreateServiceDto.Request admin1 = createAdminRequest("admin1", "admin1@test.com", "1234");
    AdminCreateServiceDto.Response admin = adminService.createAdmin(admin1);

    ManagementRequestServiceDto.Request managementCreateRequest = createManagementRequest("title1",
        "content1");
    ManagementRequestServiceDto.Response managementRequest = managementService.createManagementRequest(
        managementCreateRequest, savedMember.getMemberCode());

    Request resultRequest = createManagementRequestResult("result title", "result content",
        ACCEPTED);
    ManagementResultCreateDto.Response result = managementResultService.createResult(resultRequest,
        managementRequest.getId(), admin.getEmail());

    // when
    ManagementTotalResponseDto managementTotalData = managementTotalResponseService.getManagementTotalData(
        managementRequest.getId());

    // then
    assertThat(managementTotalData)
        .extracting("requestTitle", "requestContent", "requestStatus")
        .contains(
            "title1", "content1", ACCEPTED
        );

    assertThat(managementTotalData)
        .extracting(ManagementTotalResponseDto::getResultDto)
        .extracting(ManagementResultDto::getAdminName, ManagementResultDto::getResultPostTitle,
            ManagementResultDto::getResultPostContent, ManagementResultDto::getResultStatus)
        .contains(
            "admin1", "result title", "result content", ACCEPTED
        );
  }

  @DisplayName("답안이 등록되지 않은 경우 답안에는 null 이 들어간다.")
  @Test
  public void createTotalResponseWithResultIsNull() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest = createMemberRequest("kim", "980101", "경남", "김해",
        "삼계로");
    Response savedMember = memberService.createMember(memberRequest);

    AdminCreateServiceDto.Request admin1 = createAdminRequest("admin1", "admin1@test.com", "1234");
    AdminCreateServiceDto.Response admin = adminService.createAdmin(admin1);

    ManagementRequestServiceDto.Request managementCreateRequest = createManagementRequest("title1",
        "content1");
    ManagementRequestServiceDto.Response managementRequest = managementService.createManagementRequest(
        managementCreateRequest, savedMember.getMemberCode());

    // when
    ManagementTotalResponseDto managementTotalData = managementTotalResponseService.getManagementTotalData(
        managementRequest.getId());

    // then
    assertThat(managementTotalData)
        .extracting("requestTitle", "requestContent", "requestStatus")
        .contains(
            "title1", "content1", AWAIT
        );

    assertThat(managementTotalData.getResultDto()).isNull();
  }

  private static Request createManagementRequestResult(String title, String content,
      RequestStatus requestStatus) {
    return Request.builder()
        .resultPostTitle(title)
        .resultPostContent(content)
        .resultStatus(requestStatus)
        .build();
  }

  private ManagementRequestServiceDto.Request createManagementRequest(String title,
      String content) {
    return ManagementRequestServiceDto.Request.builder()
        .title(title)
        .content(content)
        .build();
  }

  private MemberCreateServiceDto.Request createMemberRequest(String name, String birthdayCode,
      String legion, String city, String street) {
    return MemberCreateServiceDto.Request.builder()
        .name(name)
        .birthdayCode(birthdayCode)
        .legion(legion)
        .city(city)
        .street(street)
        .build();
  }

  private static AdminCreateServiceDto.Request createAdminRequest(String name, String email,
      String password) {
    return AdminCreateServiceDto.Request.builder()
        .name(name)
        .email(email)
        .password(password)
        .build();
  }
}