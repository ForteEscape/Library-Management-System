package com.management.library.service.result.newbook;

import static com.management.library.domain.type.RequestStatus.ACCEPTED;
import static com.management.library.domain.type.RequestStatus.REFUSED;
import static com.management.library.exception.ErrorCode.REPLY_ALREADY_EXISTS;
import static com.management.library.service.result.newbook.dto.NewBookResultCreateDto.Request;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.management.library.AbstractContainerBaseTest;
import com.management.library.domain.type.RequestStatus;
import com.management.library.exception.InvalidAccessException;
import com.management.library.repository.admin.AdministratorRepository;
import com.management.library.repository.member.MemberRepository;
import com.management.library.repository.newbook.NewBookRequestRepository;
import com.management.library.repository.newbook.NewBookRequestResultRepository;
import com.management.library.service.admin.AdminService;
import com.management.library.service.admin.dto.AdminServiceCreateDto;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.dto.MemberServiceCreateDto;
import com.management.library.service.request.RedisRequestService;
import com.management.library.service.request.newbook.NewBookService;
import com.management.library.service.request.newbook.dto.NewBookRequestServiceDto;
import com.management.library.service.result.newbook.dto.NewBookResultCreateDto.Response;
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
class NewBookResultServiceTest extends AbstractContainerBaseTest {

  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private AdministratorRepository administratorRepository;
  @Autowired
  private NewBookRequestRepository newBookRequestRepository;
  @Autowired
  private NewBookRequestResultRepository newBookRequestResultRepository;
  @Autowired
  private MemberService memberService;
  @Autowired
  private AdminService adminService;
  @Autowired
  private NewBookService newBookService;
  @Autowired
  private NewBookResultService newBookResultService;
  @Autowired
  private RedisRequestService redisRequestService;

  private static final String NEW_BOOK_REQUEST_PREFIX = "book-request-id:";

  @AfterEach
  void tearDown() {
    newBookRequestResultRepository.deleteAllInBatch();
    newBookRequestRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
    administratorRepository.deleteAllInBatch();

    for (int i = 1; i < 100; i++) {
      redisRequestService.deleteCache(NEW_BOOK_REQUEST_PREFIX + i);
    }
  }

  @DisplayName("요청에 대한 결과를 등록할 수 있다.")
  @Test
  public void createResult() throws Exception {
    // given
    MemberServiceCreateDto.Request memberRequest = createMemberRequest("kim", "980101", "경남", "김해",
        "삼계로");
    MemberServiceCreateDto.Response savedMember = memberService.createMember(memberRequest);

    AdminServiceCreateDto.Request admin1 = createAdminRequest("admin1", "admin1@test.com", "1234");
    AdminServiceCreateDto.Request admin2 = createAdminRequest("admin2", "admin2@test.com", "1234");
    adminService.createAdmin(admin1);
    adminService.createAdmin(admin2);

    NewBookRequestServiceDto.Request newBookRequestForm = createNewBookRequest("book1", "content1");
    NewBookRequestServiceDto.Response newBookRequest = newBookService.createNewBookRequest(
        newBookRequestForm, savedMember.getMemberCode());

    Request newBookRequestResult = createNewBookRequestResult("title1", "content1", ACCEPTED);

    // when
    Response result = newBookResultService.createResult(newBookRequestResult,
        newBookRequest.getId(), admin1.getEmail());

    // then
    assertThat(result)
        .extracting("newBookRequestTitle", "adminName", "resultPostTitle", "resultPostContent",
            "resultStatus")
        .contains(
            "book1", "admin1", "title1", "content1", ACCEPTED
        );
  }

  @DisplayName("하나의 요청에 2개의 답변을 작성할 수 없다.")
  @Test
  public void createResultTwice() throws Exception {
    // given
    MemberServiceCreateDto.Request memberRequest = createMemberRequest("kim", "980101", "경남", "김해",
        "삼계로");
    MemberServiceCreateDto.Response savedMember = memberService.createMember(memberRequest);

    AdminServiceCreateDto.Request admin1 = createAdminRequest("admin1", "admin1@test.com", "1234");
    AdminServiceCreateDto.Request admin2 = createAdminRequest("admin2", "admin2@test.com", "1234");
    adminService.createAdmin(admin1);
    adminService.createAdmin(admin2);

    NewBookRequestServiceDto.Request newBookRequestForm = createNewBookRequest("book1", "content1");

    NewBookRequestServiceDto.Response newBookRequest = newBookService.createNewBookRequest(
        newBookRequestForm, savedMember.getMemberCode());

    Request newBookRequestResult1 = createNewBookRequestResult("title1", "content1", ACCEPTED);
    Request newBookRequestResult2 = createNewBookRequestResult("title2", "content2", ACCEPTED);

    newBookResultService.createResult(newBookRequestResult1, newBookRequest.getId(),
        admin1.getEmail());

    // when
    // then
    assertThatThrownBy(
        () -> newBookResultService.createResult(newBookRequestResult2, newBookRequest.getId(),
            admin1.getEmail()))
        .isInstanceOf(InvalidAccessException.class)
        .extracting("errorCode", "description")
        .contains(
            REPLY_ALREADY_EXISTS, REPLY_ALREADY_EXISTS.getDescription()
        );
  }

  @DisplayName("하나의 요청에 동시에 2개의 답변을 작성할 경우 하나만 등록이 성공한다.")
  @Test
  public void createResultConcurrentProblem() throws Exception {
    // given
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    CountDownLatch latch = new CountDownLatch(2);

    MemberServiceCreateDto.Request memberRequest = createMemberRequest("kim", "980101", "경남", "김해",
        "삼계로");
    MemberServiceCreateDto.Response savedMember = memberService.createMember(memberRequest);

    AdminServiceCreateDto.Request admin1 = createAdminRequest("admin1", "admin1@test.com", "1234");
    AdminServiceCreateDto.Request admin2 = createAdminRequest("admin2", "admin2@test.com", "1234");
    adminService.createAdmin(admin1);
    adminService.createAdmin(admin2);

    NewBookRequestServiceDto.Request newBookRequestForm = createNewBookRequest("book1", "content1");
    NewBookRequestServiceDto.Response newBookRequest = newBookService.createNewBookRequest(
        newBookRequestForm, savedMember.getMemberCode());

    Request newBookRequestResult1 = createNewBookRequestResult("title1", "content1", ACCEPTED);
    Request newBookRequestResult2 = createNewBookRequestResult("title2", "content2", ACCEPTED);

    // when
    Future<Boolean> submit1 = executorService.submit(() -> {
      try {
        newBookResultService.createResult(newBookRequestResult1, newBookRequest.getId(),
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
        newBookResultService.createResult(newBookRequestResult2, newBookRequest.getId(),
            admin2.getEmail());

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
    MemberServiceCreateDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경남", "김해",
        "삼계로");
    MemberServiceCreateDto.Request memberRequest2 = createMemberRequest("park", "980101", "경남",
        "김해", "삼계로");
    MemberServiceCreateDto.Response savedMember1 = memberService.createMember(memberRequest1);
    MemberServiceCreateDto.Response savedMember2 = memberService.createMember(memberRequest2);

    AdminServiceCreateDto.Request admin1 = createAdminRequest("admin1", "admin1@test.com", "1234");
    AdminServiceCreateDto.Request admin2 = createAdminRequest("admin2", "admin2@test.com", "1234");
    adminService.createAdmin(admin1);
    adminService.createAdmin(admin2);

    NewBookRequestServiceDto.Request newBookRequestForm1 = createNewBookRequest("book1",
        "content1");
    NewBookRequestServiceDto.Request newBookRequestForm2 = createNewBookRequest("book2",
        "content2");
    NewBookRequestServiceDto.Request newBookRequestForm3 = createNewBookRequest("book3",
        "content3");

    NewBookRequestServiceDto.Response newBookRequest1 = newBookService.createNewBookRequest(
        newBookRequestForm1, savedMember1.getMemberCode());

    NewBookRequestServiceDto.Response newBookRequest2 = newBookService.createNewBookRequest(
        newBookRequestForm2, savedMember1.getMemberCode());

    NewBookRequestServiceDto.Response newBookRequest3 = newBookService.createNewBookRequest(
        newBookRequestForm3, savedMember2.getMemberCode());

    Request newBookRequestResult1 = createNewBookRequestResult("title1", "content1", REFUSED);
    Request newBookRequestResult2 = createNewBookRequestResult("title2", "content2", ACCEPTED);
    Request newBookRequestResult3 = createNewBookRequestResult("title3", "content3", ACCEPTED);

    newBookResultService.createResult(newBookRequestResult1, newBookRequest1.getId(),
        admin1.getEmail());

    newBookResultService.createResult(newBookRequestResult2, newBookRequest2.getId(),
        admin2.getEmail());

    newBookResultService.createResult(newBookRequestResult3, newBookRequest3.getId(),
        admin1.getEmail());

    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<Response> result = newBookResultService.getResultByAdminEmail(admin1.getEmail(),
        pageRequest);
    List<Response> content = result.getContent();

    // then
    assertThat(content).hasSize(2)
        .extracting("newBookRequestTitle", "adminName", "resultPostTitle", "resultPostContent",
            "resultStatus")
        .containsExactlyInAnyOrder(
            tuple("book1", "admin1", "title1", "content1", REFUSED),
            tuple("book3", "admin1", "title3", "content3", ACCEPTED)
        );
  }

  private static Request createNewBookRequestResult(String title,
      String content, RequestStatus requestStatus) {
    return Request.builder()
        .resultTitle(title)
        .resultContent(content)
        .resultStatus(requestStatus)
        .build();
  }

  private NewBookRequestServiceDto.Request createNewBookRequest(String title,
      String content) {
    return NewBookRequestServiceDto.Request.builder()
        .requestBookTitle(title)
        .requestContent(content)
        .build();
  }

  private MemberServiceCreateDto.Request createMemberRequest(String name, String birthdayCode,
      String legion, String city, String street) {
    return MemberServiceCreateDto.Request.builder()
        .name(name)
        .birthdayCode(birthdayCode)
        .legion(legion)
        .city(city)
        .street(street)
        .build();
  }

  private static AdminServiceCreateDto.Request createAdminRequest(String name, String email,
      String password) {
    return AdminServiceCreateDto.Request.builder()
        .name(name)
        .email(email)
        .password(password)
        .build();
  }
}