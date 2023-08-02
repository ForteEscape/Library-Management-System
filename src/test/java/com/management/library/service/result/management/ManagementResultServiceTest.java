package com.management.library.service.result.management;

import static com.management.library.domain.type.RequestStatus.ACCEPTED;
import static com.management.library.domain.type.RequestStatus.REFUSED;
import static com.management.library.exception.ErrorCode.REPLY_ALREADY_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.management.library.AbstractContainerBaseTest;
import com.management.library.domain.type.RequestStatus;
import com.management.library.exception.InvalidAccessException;
import com.management.library.repository.admin.AdministratorRepository;
import com.management.library.repository.management.ManagementRequestRepository;
import com.management.library.repository.management.ManagementRequestResultRepository;
import com.management.library.repository.member.MemberRepository;
import com.management.library.service.admin.AdminService;
import com.management.library.service.admin.dto.AdminCreateServiceDto;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.dto.MemberCreateServiceDto;
import com.management.library.service.member.dto.MemberCreateServiceDto.Response;
import com.management.library.service.request.RedisRequestService;
import com.management.library.service.request.management.ManagementService;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto;
import com.management.library.service.result.management.dto.ManagementResultCreateDto;
import com.management.library.service.result.management.dto.ManagementResultCreateDto.Request;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
class ManagementResultServiceTest extends AbstractContainerBaseTest {

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
  private static final String MANAGEMENT_REQUEST_PREFIX = "management-request-id:";

  @AfterEach
  void tearDown() {
    managementRequestResultRepository.deleteAllInBatch();
    managementRequestRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
    administratorRepository.deleteAllInBatch();

    for (int i = 1; i < 100; i++) {
      redisRequestService.deleteCache(MANAGEMENT_REQUEST_PREFIX + i);
    }
  }

  @DisplayName("요청에 대한 결과를 등록할 수 있다.")
  @Test
  public void createResult() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest = createMemberRequest("kim", "980101", "경남", "김해",
        "삼계로");
    Response savedMember = memberService.createMember(memberRequest);

    AdminCreateServiceDto.Request admin1 = createAdminRequest("admin1", "admin1@test.com", "1234");
    adminService.createAdmin(admin1);

    ManagementRequestServiceDto.Request managementCreateRequest = createManagementRequest("title1",
        "content1");
    ManagementRequestServiceDto.Response managementRequest = managementService.createManagementRequest(
        managementCreateRequest, savedMember.getMemberCode());

    Request resultRequest = createManagementRequestResult("result title", "result content",
        ACCEPTED);

    // when
    ManagementResultCreateDto.Response result = managementResultService.createResult(resultRequest,
        managementRequest.getId(), admin1.getEmail());

    // then
    assertThat(result)
        .extracting("managementRequestTitle", "adminName", "resultPostTitle", "resultPostContent",
            "resultStatus")
        .contains(
            "title1", "admin1", "result title", "result content", ACCEPTED
        );
  }

  @DisplayName("하나의 요청에 2개의 답변을 작성할 수 없다.")
  @Test
  public void createResultTwice() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest = createMemberRequest("kim", "980101", "경남", "김해",
        "삼계로");
    Response savedMember = memberService.createMember(memberRequest);

    AdminCreateServiceDto.Request admin1 = createAdminRequest("admin1", "admin1@test.com", "1234");
    AdminCreateServiceDto.Request admin2 = createAdminRequest("admin2", "admin2@test.com", "1234");
    adminService.createAdmin(admin1);
    adminService.createAdmin(admin2);

    ManagementRequestServiceDto.Request managementCreateRequest = createManagementRequest("title1",
        "content1");
    ManagementRequestServiceDto.Response managementRequest = managementService.createManagementRequest(
        managementCreateRequest, savedMember.getMemberCode());

    Request resultRequest1 = createManagementRequestResult("result title1", "result content1",
        ACCEPTED);
    Request resultRequest2 = createManagementRequestResult("result title2", "result content2",
        ACCEPTED);

    managementResultService.createResult(resultRequest1, managementRequest.getId(),
        admin1.getEmail());

    // when
    // then
    assertThatThrownBy(() -> managementResultService.createResult(resultRequest2,
        managementRequest.getId(), admin2.getEmail()))
        .isInstanceOf(InvalidAccessException.class)
        .extracting("errorCode", "description")
        .contains(
            REPLY_ALREADY_EXISTS, REPLY_ALREADY_EXISTS.getDescription()
        );
  }

  @DisplayName("하나의 요청에 동시점에 2개의 답변을 작성할 수 없다.")
  @Test
  public void createResultConcurrentProblem() throws Exception {
    // given
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    CountDownLatch latch = new CountDownLatch(2);

    MemberCreateServiceDto.Request memberRequest = createMemberRequest("kim", "980101", "경남", "김해",
        "삼계로");
    Response savedMember = memberService.createMember(memberRequest);

    AdminCreateServiceDto.Request admin1 = createAdminRequest("admin1", "admin1@test.com", "1234");
    AdminCreateServiceDto.Request admin2 = createAdminRequest("admin2", "admin2@test.com", "1234");
    adminService.createAdmin(admin1);
    adminService.createAdmin(admin2);

    ManagementRequestServiceDto.Request managementCreateRequest = createManagementRequest("title1",
        "content1");

    ManagementRequestServiceDto.Response managementRequest = managementService.createManagementRequest(
        managementCreateRequest, savedMember.getMemberCode());

    Request resultRequest1 = createManagementRequestResult("result title1", "result content1",
        ACCEPTED);
    Request resultRequest2 = createManagementRequestResult("result title2", "result content2",
        ACCEPTED);

    // when
    Future<Boolean> submit1 = executorService.submit(() -> {
      try {
        managementResultService.createResult(resultRequest1, managementRequest.getId(),
            admin1.getEmail());

        return true;
      } catch (InvalidAccessException e) {
        return false;
      } finally {
        latch.countDown();
      }
    });

    Future<Boolean> submit2 = executorService.submit(() -> {
      try {
        managementResultService.createResult(resultRequest2, managementRequest.getId(),
            admin1.getEmail());

        return true;
      } catch (InvalidAccessException e) {
        return false;
      } finally {
        latch.countDown();
      }
    });
    latch.await();

    List<Boolean> result = List.of(submit1.get(), submit2.get());

    // then
    assertThat(result)
        .contains(true, false);

  }

  @DisplayName("관리자의 이메일로 해당 관리자가 작성한 요청 결과를 조회할 수 있다.")
  @Test
  public void getResultByAdminEmail() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경남", "김해",
        "삼계로");
    MemberCreateServiceDto.Request memberRequest2 = createMemberRequest("park", "980101", "경남",
        "김해", "삼계로");
    Response savedMember1 = memberService.createMember(memberRequest1);
    Response savedMember2 = memberService.createMember(memberRequest2);

    AdminCreateServiceDto.Request admin1 = createAdminRequest("admin1", "admin1@test.com", "1234");
    AdminCreateServiceDto.Request admin2 = createAdminRequest("admin2", "admin2@test.com", "1234");
    adminService.createAdmin(admin1);
    adminService.createAdmin(admin2);

    ManagementRequestServiceDto.Request managementCreateRequest1 = createManagementRequest("title1",
        "content1");
    ManagementRequestServiceDto.Request managementCreateRequest2 = createManagementRequest("title2",
        "content2");
    ManagementRequestServiceDto.Request managementCreateRequest3 = createManagementRequest("title3",
        "content3");

    ManagementRequestServiceDto.Response managementRequest1 = managementService.createManagementRequest(
        managementCreateRequest1, savedMember1.getMemberCode());

    ManagementRequestServiceDto.Response managementRequest2 = managementService.createManagementRequest(
        managementCreateRequest2, savedMember2.getMemberCode());

    managementService.createManagementRequest(managementCreateRequest3,
        savedMember1.getMemberCode());

    Request resultRequest1 = createManagementRequestResult("result title1", "result content1",
        ACCEPTED);
    Request resultRequest2 = createManagementRequestResult("result title2", "result content2",
        REFUSED);

    managementResultService.createResult(resultRequest1, managementRequest1.getId(),
        admin1.getEmail());
    managementResultService.createResult(resultRequest2, managementRequest2.getId(),
        admin1.getEmail());

    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<ManagementResultCreateDto.Response> result = managementResultService.getResultByAdminEmail(
        admin1.getEmail(), pageRequest);
    List<ManagementResultCreateDto.Response> content = result.getContent();

    // then
    assertThat(content).hasSize(2)
        .extracting("managementRequestTitle", "adminName", "resultPostTitle", "resultPostContent",
            "resultStatus")
        .containsExactlyInAnyOrder(
            tuple("title1", "admin1", "result title1", "result content1", ACCEPTED),
            tuple("title2", "admin1", "result title2", "result content2", REFUSED)
        );
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