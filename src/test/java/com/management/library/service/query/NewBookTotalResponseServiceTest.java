package com.management.library.service.query;

import static com.management.library.domain.type.RequestStatus.ACCEPTED;
import static com.management.library.domain.type.RequestStatus.AWAIT;
import static org.assertj.core.api.Assertions.assertThat;

import com.management.library.AbstractContainerBaseTest;
import com.management.library.domain.type.RequestStatus;
import com.management.library.repository.admin.AdministratorRepository;
import com.management.library.repository.member.MemberRepository;
import com.management.library.repository.newbook.NewBookRequestRepository;
import com.management.library.repository.newbook.NewBookRequestResultRepository;
import com.management.library.service.admin.AdminService;
import com.management.library.service.admin.dto.AdminServiceCreateDto;
import com.management.library.service.admin.dto.AdminServiceCreateDto.Response;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.dto.MemberServiceCreateDto;
import com.management.library.service.query.dto.NewBookResultDto;
import com.management.library.service.query.dto.NewBookTotalResponseDto;
import com.management.library.service.request.RedisRequestService;
import com.management.library.service.request.newbook.NewBookService;
import com.management.library.service.request.newbook.dto.NewBookRequestServiceDto;
import com.management.library.service.result.newbook.NewBookResultService;
import com.management.library.service.result.newbook.dto.NewBookResultCreateDto;
import com.management.library.service.result.newbook.dto.NewBookResultCreateDto.Request;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class NewBookTotalResponseServiceTest extends AbstractContainerBaseTest {

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
  @Autowired
  private NewBookTotalResponseService newBookTotalResponseService;

  private static final String NEW_BOOK_REQUEST_PREFIX = "book-request-id:";
  private static final String NEW_BOOK_CACHE_KEY = "book-request-count:";

  @AfterEach
  void tearDown() {
    newBookRequestResultRepository.deleteAllInBatch();
    newBookRequestRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
    administratorRepository.deleteAllInBatch();

    redisRequestService.deleteCache(NEW_BOOK_CACHE_KEY);

    for (int i = 1; i < 100; i++) {
      redisRequestService.deleteCache(NEW_BOOK_REQUEST_PREFIX + i);
    }
  }

  @DisplayName("신간 요청에 대한 내역과 그 답안까지 모두 가져올 수 있다.")
  @Test
  public void createTotalResult() throws Exception {
    // given
    MemberServiceCreateDto.Request memberRequest = createMemberRequest("kim", "980101", "경남", "김해",
        "삼계로");
    MemberServiceCreateDto.Response savedMember = memberService.createMember(memberRequest);

    AdminServiceCreateDto.Request admin1 = createAdminRequest("admin1", "admin1@test.com", "1234");
    Response admin = adminService.createAdmin(admin1);

    NewBookRequestServiceDto.Request newBookRequestForm = createNewBookRequest("book1", "content1");
    NewBookRequestServiceDto.Response newBookRequest = newBookService.createNewBookRequest(
        newBookRequestForm, savedMember.getMemberCode());

    Request newBookRequestResult = createNewBookRequestResult("title1", "content1", ACCEPTED);
    NewBookResultCreateDto.Response result = newBookResultService.createResult(newBookRequestResult,
        newBookRequest.getId(), admin.getEmail());
    // when
    NewBookTotalResponseDto newBookTotalResponse = newBookTotalResponseService.getNewBookTotalResponse(
        newBookRequest.getId());

    // then
    assertThat(newBookTotalResponse)
        .extracting("requestBookTitle", "requestContent", "requestStatus")
        .contains(
            "book1", "content1", ACCEPTED
        );

    assertThat(newBookTotalResponse)
        .extracting(NewBookTotalResponseDto::getResultDto)
        .extracting(NewBookResultDto::getAdminName, NewBookResultDto::getResultPostTitle,
            NewBookResultDto::getResultPostContent, NewBookResultDto::getResultStatus)
        .contains(
            "admin1", "title1", "content1", ACCEPTED
        );
  }

  @DisplayName("답안이 등록되어 있지 않는 경우 답안에 null 이 들어간다")
  @Test
  public void createTotalResultWithNullResult() throws Exception {
    // given
    MemberServiceCreateDto.Request memberRequest = createMemberRequest("kim", "980101", "경남", "김해",
        "삼계로");
    MemberServiceCreateDto.Response savedMember = memberService.createMember(memberRequest);

    AdminServiceCreateDto.Request admin1 = createAdminRequest("admin1", "admin1@test.com", "1234");
    Response admin = adminService.createAdmin(admin1);

    NewBookRequestServiceDto.Request newBookRequestForm = createNewBookRequest("book1", "content1");
    NewBookRequestServiceDto.Response newBookRequest = newBookService.createNewBookRequest(
        newBookRequestForm, savedMember.getMemberCode());

    // when
    NewBookTotalResponseDto newBookTotalResponse = newBookTotalResponseService.getNewBookTotalResponse(
        newBookRequest.getId());

    // then
    assertThat(newBookTotalResponse)
        .extracting("requestBookTitle", "requestContent", "requestStatus")
        .contains(
            "book1", "content1", AWAIT
        );

    assertThat(newBookTotalResponse.getResultDto()).isNull();
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