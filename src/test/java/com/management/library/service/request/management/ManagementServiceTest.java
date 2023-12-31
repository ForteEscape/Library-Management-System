package com.management.library.service.request.management;

import static com.management.library.domain.type.RequestStatus.ACCEPTED;
import static com.management.library.domain.type.RequestStatus.AWAIT;
import static com.management.library.domain.type.RequestStatus.REFUSED;
import static com.management.library.exception.ErrorCode.MANAGEMENT_REQUEST_COUNT_EXCEEDED;
import static com.management.library.exception.ErrorCode.REQUEST_NOT_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.management.library.AbstractContainerBaseTest;
import com.management.library.domain.management.ManagementRequest;
import com.management.library.controller.dto.RequestSearchCond;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.exception.RequestLimitExceededException;
import com.management.library.repository.management.ManagementRequestRepository;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.dto.MemberServiceCreateDto;
import com.management.library.service.request.RedisRequestService;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto.Request;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto.Response;
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
class ManagementServiceTest extends AbstractContainerBaseTest {

  @Autowired
  private ManagementService managementService;
  @Autowired
  private MemberService memberService;
  @Autowired
  private ManagementRequestRepository managementRequestRepository;
  @Autowired
  private RedisRequestService redisRequestService;
  private static final String KEY = "management-request-count:";
  private static final String MANAGEMENT_REQUEST_PREFIX = "management-request-id:";

  @AfterEach
  void tearDown() {
    redisRequestService.deleteCache(KEY);

    for (int i = 0; i < 100; i++){
      redisRequestService.deleteCache(MANAGEMENT_REQUEST_PREFIX + i);
    }
  }

  @DisplayName("새로운 운영 개선 요청을 등록할 수 있다.")
  @Test
  public void createManagementRequest() throws Exception {
    // given
    MemberServiceCreateDto.Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    MemberServiceCreateDto.Response memberResponse = memberService.createMember(request1);

    Request managementRequest = getManagementRequest("title1", "content1");

    // when
    Response managementResponse = managementService.createManagementRequest(
        managementRequest, memberResponse.getMemberCode());

    // then
    assertThat(managementResponse)
        .extracting("title", "content", "memberName", "requestStatus")
        .contains(
            "title1", "content1", "kim", AWAIT
        );
  }

  @DisplayName("1달에 요청을 6번 이상 등록 시 예외 발생")
  @Test
  public void createManagementRequestWithSixTimes() throws Exception {
    // given
    MemberServiceCreateDto.Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    MemberServiceCreateDto.Response memberResponse = memberService.createMember(request1);

    Request managementRequest = getManagementRequest("title1", "content1");

    // when
    for (int i = 0; i < 5; i++) {
      managementService.createManagementRequest(managementRequest, memberResponse.getMemberCode());
    }

    // then
    assertThatThrownBy(() -> managementService.createManagementRequest(managementRequest,
        memberResponse.getMemberCode()))
        .isInstanceOf(RequestLimitExceededException.class)
        .extracting("errorCode", "description")
        .contains(
            MANAGEMENT_REQUEST_COUNT_EXCEEDED, MANAGEMENT_REQUEST_COUNT_EXCEEDED.getDescription()
        );
  }

  @DisplayName("특정 회원의 운영 요청 데이터를 가져올 수 있다.")
  @Test
  public void getMemberManageRequest() throws Exception {
    // given
    MemberServiceCreateDto.Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    MemberServiceCreateDto.Response memberResponse = memberService.createMember(request1);

    Request managementRequest1 = getManagementRequest("title1", "content1");
    Request managementRequest2 = getManagementRequest("title2", "content2");
    Request managementRequest3 = getManagementRequest("title3", "content3");

    managementService.createManagementRequest(managementRequest1, memberResponse.getMemberCode());
    managementService.createManagementRequest(managementRequest2, memberResponse.getMemberCode());
    managementService.createManagementRequest(managementRequest3, memberResponse.getMemberCode());

    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<Response> result = managementService.getMemberManagementRequest(
        memberResponse.getMemberCode(), pageRequest);
    List<Response> content = result.getContent();

    // then
    assertThat(content).hasSize(3)
        .extracting("title", "content", "memberName", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("title1", "content1", "kim", AWAIT),
            tuple("title2", "content2", "kim", AWAIT),
            tuple("title3", "content3", "kim", AWAIT)
        );
  }

  @DisplayName("모든 회원의 운영 요청 데이터를 가져올 수 있다.")
  @Test
  public void getAllManageRequest() throws Exception {
    // given
    MemberServiceCreateDto.Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    MemberServiceCreateDto.Request request2 = createRequest("park", "980101", "경상남도", "김해시", "삼계로");
    MemberServiceCreateDto.Response memberResponse1 = memberService.createMember(request1);
    MemberServiceCreateDto.Response memberResponse2 = memberService.createMember(request2);

    Request managementRequest1 = getManagementRequest("title1", "content1");
    Request managementRequest2 = getManagementRequest("title2", "content2");
    Request managementRequest3 = getManagementRequest("title3", "content3");
    Request managementRequest4 = getManagementRequest("title4", "content4");
    Request managementRequest5 = getManagementRequest("title5", "content5");

    List<Request> managementRequestList = List.of(managementRequest1, managementRequest2,
        managementRequest3, managementRequest4, managementRequest5);

    for (int i = 0; i < 5; i++) {
      if (i < 3) {
        managementService.createManagementRequest(managementRequestList.get(i),
            memberResponse1.getMemberCode());
      } else {
        managementService.createManagementRequest(managementRequestList.get(i),
            memberResponse2.getMemberCode());
      }
    }

    RequestSearchCond cond = new RequestSearchCond();
    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<Response> result = managementService.getAllManagementRequest(cond, pageRequest);
    List<Response> content = result.getContent();

    // then
    assertThat(content).hasSize(5)
        .extracting("title", "content", "memberName", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("title1", "content1", "kim", AWAIT),
            tuple("title2", "content2", "kim", AWAIT),
            tuple("title3", "content3", "kim", AWAIT),
            tuple("title4", "content4", "park", AWAIT),
            tuple("title5", "content5", "park", AWAIT)
        );
  }

  @DisplayName("모든 회원의 반영된 운영 요청 데이터를 가져올 수 있다.")
  @Test
  public void getAllManageRequestWithAccepted() throws Exception {
    // given
    MemberServiceCreateDto.Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    MemberServiceCreateDto.Request request2 = createRequest("park", "980101", "경상남도", "김해시", "삼계로");
    MemberServiceCreateDto.Response memberResponse1 = memberService.createMember(request1);
    MemberServiceCreateDto.Response memberResponse2 = memberService.createMember(request2);

    List<Request> managementRequestList = new ArrayList<>();
    for (int i = 1; i <= 5; i++) {
      managementRequestList.add(getManagementRequest("title" + i, "content" + i));
    }

    List<Response> savedRequest = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      if (i < 3) {
        savedRequest.add(managementService.createManagementRequest(managementRequestList.get(i),
            memberResponse1.getMemberCode()));
      } else {
        savedRequest.add(managementService.createManagementRequest(managementRequestList.get(i),
            memberResponse2.getMemberCode()));
      }
    }

    ManagementRequest result1 = managementRequestRepository.findById(savedRequest.get(1).getId())
        .orElseThrow(() -> new NoSuchElementExistsException(REQUEST_NOT_EXISTS));
    ManagementRequest result2 = managementRequestRepository.findById(savedRequest.get(3).getId())
        .orElseThrow(() -> new NoSuchElementExistsException(REQUEST_NOT_EXISTS));

    result1.changeRequestStatus(ACCEPTED);
    result2.changeRequestStatus(ACCEPTED);

    RequestSearchCond cond = new RequestSearchCond();
    cond.setRequestStatus(ACCEPTED);
    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<Response> result = managementService.getAllManagementRequest(cond, pageRequest);
    List<Response> content = result.getContent();

    // then
    assertThat(content).hasSize(2)
        .extracting("title", "content", "memberName", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("title2", "content2", "kim", ACCEPTED),
            tuple("title4", "content4", "park", ACCEPTED)
        );
  }

  @DisplayName("모든 회원의 거절된 운영 요청 데이터를 가져올 수 있다.")
  @Test
  public void getAllManageRequestWithRefused() throws Exception {
    // given
    MemberServiceCreateDto.Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    MemberServiceCreateDto.Request request2 = createRequest("park", "980101", "경상남도", "김해시", "삼계로");
    MemberServiceCreateDto.Response memberResponse1 = memberService.createMember(request1);
    MemberServiceCreateDto.Response memberResponse2 = memberService.createMember(request2);

    List<Request> managementRequestList = new ArrayList<>();
    for (int i = 1; i <= 5; i++) {
      managementRequestList.add(getManagementRequest("title" + i, "content" + i));
    }

    List<Response> savedRequest = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      if (i < 3) {
        savedRequest.add(managementService.createManagementRequest(managementRequestList.get(i),
            memberResponse1.getMemberCode()));
      } else {
        savedRequest.add(managementService.createManagementRequest(managementRequestList.get(i),
            memberResponse2.getMemberCode()));
      }
    }

    ManagementRequest result1 = managementRequestRepository.findById(savedRequest.get(1).getId())
        .orElseThrow(() -> new NoSuchElementExistsException(REQUEST_NOT_EXISTS));
    ManagementRequest result2 = managementRequestRepository.findById(savedRequest.get(3).getId())
        .orElseThrow(() -> new NoSuchElementExistsException(REQUEST_NOT_EXISTS));

    result1.changeRequestStatus(REFUSED);
    result2.changeRequestStatus(REFUSED);

    RequestSearchCond cond = new RequestSearchCond();
    cond.setRequestStatus(REFUSED);
    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<Response> result = managementService.getAllManagementRequest(cond, pageRequest);
    List<Response> content = result.getContent();

    // then
    assertThat(content).hasSize(2)
        .extracting("title", "content", "memberName", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("title2", "content2", "kim", REFUSED),
            tuple("title4", "content4", "park", REFUSED)
        );
  }

  @DisplayName("모든 회원의 진행 중인 운영 요청 데이터를 가져올 수 있다.")
  @Test
  public void getAllManageRequestWithAwait() throws Exception {
    // given
    MemberServiceCreateDto.Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    MemberServiceCreateDto.Request request2 = createRequest("park", "980101", "경상남도", "김해시", "삼계로");
    MemberServiceCreateDto.Response memberResponse1 = memberService.createMember(request1);
    MemberServiceCreateDto.Response memberResponse2 = memberService.createMember(request2);

    List<Request> managementRequestList = new ArrayList<>();
    for (int i = 1; i <= 5; i++) {
      managementRequestList.add(getManagementRequest("title" + i, "content" + i));
    }

    List<Response> savedRequest = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      if (i < 3) {
        savedRequest.add(managementService.createManagementRequest(managementRequestList.get(i),
            memberResponse1.getMemberCode()));
      } else {
        savedRequest.add(managementService.createManagementRequest(managementRequestList.get(i),
            memberResponse2.getMemberCode()));
      }
    }

    ManagementRequest result1 = managementRequestRepository.findById(savedRequest.get(1).getId())
        .orElseThrow(() -> new NoSuchElementExistsException(REQUEST_NOT_EXISTS));
    ManagementRequest result2 = managementRequestRepository.findById(savedRequest.get(3).getId())
        .orElseThrow(() -> new NoSuchElementExistsException(REQUEST_NOT_EXISTS));

    result1.changeRequestStatus(REFUSED);
    result2.changeRequestStatus(REFUSED);

    RequestSearchCond cond = new RequestSearchCond();
    cond.setRequestStatus(AWAIT);
    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<Response> result = managementService.getAllManagementRequest(cond, pageRequest);
    List<Response> content = result.getContent();

    // then
    assertThat(content).hasSize(3)
        .extracting("title", "content", "memberName", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("title1", "content1", "kim", AWAIT),
            tuple("title3", "content3", "kim", AWAIT),
            tuple("title5", "content5", "park", AWAIT)
        );
  }

  private static Request getManagementRequest(String title, String content) {
    return Request.builder()
        .title(title)
        .content(content)
        .build();
  }

  private static MemberServiceCreateDto.Request createRequest(String name, String birthdayCode,
      String legion, String city, String street) {
    return MemberServiceCreateDto.Request.builder()
        .name(name)
        .birthdayCode(birthdayCode)
        .legion(legion)
        .city(city)
        .street(street)
        .build();
  }
}