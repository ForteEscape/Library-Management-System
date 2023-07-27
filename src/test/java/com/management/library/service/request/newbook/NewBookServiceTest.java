package com.management.library.service.request.newbook;

import static com.management.library.domain.type.RequestStatus.ACCEPTED;
import static com.management.library.domain.type.RequestStatus.AWAIT;
import static com.management.library.domain.type.RequestStatus.REFUSED;
import static com.management.library.exception.ErrorCode.NEW_BOOK_REQUEST_COUNT_EXCEEDED;
import static com.management.library.exception.ErrorCode.REQUEST_NOT_EXISTS;
import static com.management.library.service.request.newbook.dto.NewBookRequestServiceDto.Request;
import static com.management.library.service.request.newbook.dto.NewBookRequestServiceDto.Response;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.management.library.AbstractContainerBaseTest;
import com.management.library.domain.newbook.NewBookRequest;
import com.management.library.dto.RequestSearchCond;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.exception.RequestLimitExceededException;
import com.management.library.repository.newbook.NewBookRequestRepository;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.dto.MemberCreateServiceDto;
import com.management.library.service.request.RedisRequestService;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
class NewBookServiceTest extends AbstractContainerBaseTest {

  @Autowired
  private MemberService memberService;
  @Autowired
  private NewBookService newBookService;
  @Autowired
  private NewBookRequestRepository newBookRequestRepository;
  @Autowired
  private RedisRequestService redisRequestService;
  private static final String NEW_BOOK_CACHE_KEY = "book-request-count:";

  @AfterEach
  void tearDown() {
    redisRequestService.deleteCache(NEW_BOOK_CACHE_KEY);
  }

  @DisplayName("신간 요청을 생성할 수 있다.")
  @Test
  public void createNewBookRequest() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");

    MemberCreateServiceDto.Response savedMember = memberService.createMember(memberRequest);
    Request newBookRequest = createNewBookRequest("신", "신을 요청합니다");

    // when
    Response response = newBookService.createNewBookRequest(newBookRequest,
        savedMember.getMemberCode());

    // then
    assertThat(response)
        .extracting("memberName", "requestBookTitle", "requestContent", "requestStatus")
        .contains(
            "kim", "신", "신을 요청합니다", AWAIT
        );
  }

  @DisplayName("신간 요청을 5회 초과하여 생성할 수 없다.")
  @Test
  public void createNewBookRequestExceedLimit() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");

    MemberCreateServiceDto.Response savedMember = memberService.createMember(memberRequest);

    for (int i = 0; i < 5; i++){
      String bookName = "book" + (i + 1);
      Request request = createNewBookRequest(bookName, bookName + " 을 요청합니다.");
      newBookService.createNewBookRequest(request, savedMember.getMemberCode());
    }
    Request newBookRequest = createNewBookRequest("신", "신을 요청합니다");

    // when
    // then
    assertThatThrownBy(() -> newBookService.createNewBookRequest(newBookRequest,
        savedMember.getMemberCode()))
        .isInstanceOf(RequestLimitExceededException.class)
        .extracting("errorCode", "description")
        .contains(
            NEW_BOOK_REQUEST_COUNT_EXCEEDED, NEW_BOOK_REQUEST_COUNT_EXCEEDED.getDescription()
        );
  }

  @DisplayName("특정 회원이 등록한 신간 요청을 조회할 수 있다.")
  @Test
  public void getMemberNewBookRequest() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Request memberRequest2 = createMemberRequest("park", "980505", "경상남도",
        "김해시", "삼계로");

    MemberCreateServiceDto.Response member1 = memberService.createMember(memberRequest1);
    MemberCreateServiceDto.Response member2 = memberService.createMember(memberRequest2);

    // member1의 신간 도서 요청 등록
    for (int i = 0; i < 3; i++){
      String bookName = "book" + (i + 1);
      Request request = createNewBookRequest(bookName, bookName + " 을 요청합니다.");
      newBookService.createNewBookRequest(request, member1.getMemberCode());
    }

    // member2의 신간 도서 요청 등록
    for (int i = 3; i < 6; i++){
      String bookName = "book" + (i + 1);
      Request request = createNewBookRequest(bookName, bookName + " 을 요청합니다.");
      newBookService.createNewBookRequest(request, member2.getMemberCode());
    }

    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<Response> result = newBookService.getMemberNewBookRequest(
        member1.getMemberCode(), pageRequest);
    List<Response> content = result.getContent();

    // then
    assertThat(content).hasSize(3)
        .extracting("memberName", "requestBookTitle", "requestContent", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("kim", "book1", "book1 을 요청합니다.", AWAIT),
            tuple("kim", "book2", "book2 을 요청합니다.", AWAIT),
            tuple("kim", "book3", "book3 을 요청합니다.", AWAIT)
        );
  }

  @DisplayName("모든 회원이 등록한 신간 요청을 조회할 수 있다.")
  @Test
  public void getAllNewBookRequest() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Request memberRequest2 = createMemberRequest("park", "980505", "경상남도",
        "김해시", "삼계로");

    MemberCreateServiceDto.Response member1 = memberService.createMember(memberRequest1);
    MemberCreateServiceDto.Response member2 = memberService.createMember(memberRequest2);

    // member1의 신간 도서 요청 등록
    for (int i = 0; i < 3; i++){
      String bookName = "book" + (i + 1);
      Request request = createNewBookRequest(bookName, bookName + " 을 요청합니다.");
      newBookService.createNewBookRequest(request, member1.getMemberCode());
    }

    // member2의 신간 도서 요청 등록
    for (int i = 3; i < 6; i++){
      String bookName = "book" + (i + 1);
      Request request = createNewBookRequest(bookName, bookName + " 을 요청합니다.");
      newBookService.createNewBookRequest(request, member2.getMemberCode());
    }

    PageRequest pageRequest = PageRequest.of(0, 5);
    RequestSearchCond cond = new RequestSearchCond();

    // when
    Page<Response> result = newBookService.getAllNewBookRequest(cond, pageRequest);
    List<Response> content = result.getContent();

    // then
    assertThat(content).hasSize(5)
        .extracting("memberName", "requestBookTitle", "requestContent", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("kim", "book1", "book1 을 요청합니다.", AWAIT),
            tuple("kim", "book2", "book2 을 요청합니다.", AWAIT),
            tuple("kim", "book3", "book3 을 요청합니다.", AWAIT),
            tuple("park", "book4", "book4 을 요청합니다.", AWAIT),
            tuple("park", "book5", "book5 을 요청합니다.", AWAIT)
        );
  }

  @DisplayName("모든 회원의 수락된 신간 요청을 조회할 수 있다.")
  @Test
  public void getAllNewBookRequestWithAccepted() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Request memberRequest2 = createMemberRequest("park", "980505", "경상남도",
        "김해시", "삼계로");

    MemberCreateServiceDto.Response member1 = memberService.createMember(memberRequest1);
    MemberCreateServiceDto.Response member2 = memberService.createMember(memberRequest2);

    List<Response> response = new ArrayList<>();

    // member1의 신간 도서 요청 등록
    for (int i = 0; i < 3; i++){
      String bookName = "book" + (i + 1);
      Request request = createNewBookRequest(bookName, bookName + " 을 요청합니다.");
      Response newBookRequest = newBookService.createNewBookRequest(request,
          member1.getMemberCode());

      response.add(newBookRequest);
    }

    // member2의 신간 도서 요청 등록
    for (int i = 3; i < 6; i++){
      String bookName = "book" + (i + 1);
      Request request = createNewBookRequest(bookName, bookName + " 을 요청합니다.");
      Response newBookRequest = newBookService.createNewBookRequest(request,
          member2.getMemberCode());

      response.add(newBookRequest);
    }

    for (int i = 0; i <= 4; i += 2){
      NewBookRequest request = newBookRequestRepository.findById(response.get(i).getId())
          .orElseThrow(() -> new NoSuchElementExistsException(REQUEST_NOT_EXISTS));

      request.changeRequestStatus(ACCEPTED);
    }

    PageRequest pageRequest = PageRequest.of(0, 5);
    RequestSearchCond cond = new RequestSearchCond();
    cond.setRequestStatus(ACCEPTED);

    // when
    Page<Response> result = newBookService.getAllNewBookRequest(cond, pageRequest);
    List<Response> content = result.getContent();

    // then
    assertThat(content).hasSize(3)
        .extracting("memberName", "requestBookTitle", "requestContent", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("kim", "book1", "book1 을 요청합니다.", ACCEPTED),
            tuple("kim", "book3", "book3 을 요청합니다.", ACCEPTED),
            tuple("park", "book5", "book5 을 요청합니다.", ACCEPTED)
        );
  }

  @DisplayName("모든 회원의 거절된 신간 요청을 조회할 수 있다.")
  @Test
  public void getAllNewBookRequestWithRefused() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Request memberRequest2 = createMemberRequest("park", "980505", "경상남도",
        "김해시", "삼계로");

    MemberCreateServiceDto.Response member1 = memberService.createMember(memberRequest1);
    MemberCreateServiceDto.Response member2 = memberService.createMember(memberRequest2);

    List<Response> response = new ArrayList<>();

    // member1의 신간 도서 요청 등록
    for (int i = 0; i < 3; i++){
      String bookName = "book" + (i + 1);
      Request request = createNewBookRequest(bookName, bookName + " 을 요청합니다.");
      Response newBookRequest = newBookService.createNewBookRequest(request,
          member1.getMemberCode());

      response.add(newBookRequest);
    }

    // member2의 신간 도서 요청 등록
    for (int i = 3; i < 6; i++){
      String bookName = "book" + (i + 1);
      Request request = createNewBookRequest(bookName, bookName + " 을 요청합니다.");
      Response newBookRequest = newBookService.createNewBookRequest(request,
          member2.getMemberCode());

      response.add(newBookRequest);
    }

    for (int i = 0; i <= 4; i += 2){
      NewBookRequest request = newBookRequestRepository.findById(response.get(i).getId())
          .orElseThrow(() -> new NoSuchElementExistsException(REQUEST_NOT_EXISTS));

      request.changeRequestStatus(REFUSED);
    }

    PageRequest pageRequest = PageRequest.of(0, 5);
    RequestSearchCond cond = new RequestSearchCond();
    cond.setRequestStatus(REFUSED);

    // when
    Page<Response> result = newBookService.getAllNewBookRequest(cond, pageRequest);
    List<Response> content = result.getContent();

    // then
    assertThat(content).hasSize(3)
        .extracting("memberName", "requestBookTitle", "requestContent", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("kim", "book1", "book1 을 요청합니다.", REFUSED),
            tuple("kim", "book3", "book3 을 요청합니다.", REFUSED),
            tuple("park", "book5", "book5 을 요청합니다.", REFUSED)
        );
  }

  @DisplayName("모든 회원의 대기 중인 신간 요청을 조회할 수 있다.")
  @Test
  public void getAllNewBookRequestWithAwait() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Request memberRequest2 = createMemberRequest("park", "980505", "경상남도",
        "김해시", "삼계로");

    MemberCreateServiceDto.Response member1 = memberService.createMember(memberRequest1);
    MemberCreateServiceDto.Response member2 = memberService.createMember(memberRequest2);

    // member1의 신간 도서 요청 등록
    for (int i = 0; i < 3; i++){
      String bookName = "book" + (i + 1);
      Request request = createNewBookRequest(bookName, bookName + " 을 요청합니다.");
      newBookService.createNewBookRequest(request, member1.getMemberCode());
    }

    // member2의 신간 도서 요청 등록
    for (int i = 3; i < 6; i++){
      String bookName = "book" + (i + 1);
      Request request = createNewBookRequest(bookName, bookName + " 을 요청합니다.");
      newBookService.createNewBookRequest(request, member2.getMemberCode());
    }

    PageRequest pageRequest = PageRequest.of(0, 5);
    RequestSearchCond cond = new RequestSearchCond();
    cond.setRequestStatus(AWAIT);

    // when
    Page<Response> result = newBookService.getAllNewBookRequest(cond, pageRequest);
    List<Response> content = result.getContent();

    // then
    assertThat(content).hasSize(5)
        .extracting("memberName", "requestBookTitle", "requestContent", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("kim", "book1", "book1 을 요청합니다.", AWAIT),
            tuple("kim", "book2", "book2 을 요청합니다.", AWAIT),
            tuple("kim", "book3", "book3 을 요청합니다.", AWAIT),
            tuple("park", "book4", "book4 을 요청합니다.", AWAIT),
            tuple("park", "book5", "book5 을 요청합니다.", AWAIT)
        );
  }

  public Request createNewBookRequest(String requestBookTitle, String requestContent) {
    return Request.builder()
        .requestBookTitle(requestBookTitle)
        .requestContent(requestContent)
        .build();
  }

  private static MemberCreateServiceDto.Request createMemberRequest(String name,
      String birthdayCode, String legion, String city, String street) {
    return MemberCreateServiceDto.Request.builder()
        .name(name)
        .birthdayCode(birthdayCode)
        .legion(legion)
        .city(city)
        .street(street)
        .build();
  }
}