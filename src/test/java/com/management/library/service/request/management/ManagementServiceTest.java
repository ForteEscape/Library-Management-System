package com.management.library.service.request.management;

import static com.management.library.domain.type.RequestStatus.AWAIT;
import static com.management.library.exception.ErrorCode.MANAGEMENT_REQUEST_COUNT_EXCEEDED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.management.library.AbstractContainerBaseTest;
import com.management.library.exception.RequestLimitExceededException;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.dto.MemberCreateServiceDto;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto.Request;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto.Response;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
class ManagementServiceTest extends AbstractContainerBaseTest {

  @Autowired
  private ManagementService managementService;
  @Autowired
  private MemberService memberService;
  private static final String KEY = "management-request-count:";

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @AfterEach
  void tearDown() {
    redisTemplate.delete(KEY);
  }

  @DisplayName("새로운 운영 개선 요청을 등록할 수 있다.")
  @Test
  public void createManagementRequest() throws Exception {
    // given
    MemberCreateServiceDto.Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    MemberCreateServiceDto.Response memberResponse = memberService.createMember(request1);

    Request managementRequest = getManagementRequest("title1", "content1");

    // when
    Response managementResponse = managementService.createManagementRequest(
        managementRequest, memberResponse.getMemberCode());

    log.info(managementResponse.getTitle());
    log.info(managementResponse.getContent());
    log.info(managementResponse.getMemberName());
    log.info(String.valueOf(managementResponse.getRequestStatus()));

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
    MemberCreateServiceDto.Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    MemberCreateServiceDto.Response memberResponse = memberService.createMember(request1);

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
    MemberCreateServiceDto.Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    MemberCreateServiceDto.Response memberResponse = memberService.createMember(request1);

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

  private static Request getManagementRequest(String title, String content) {
    return Request.builder()
        .title(title)
        .content(content)
        .build();
  }

  private static MemberCreateServiceDto.Request createRequest(String name, String birthdayCode,
      String legion, String city, String street) {
    return MemberCreateServiceDto.Request.builder()
        .name(name)
        .birthdayCode(birthdayCode)
        .legion(legion)
        .city(city)
        .street(street)
        .build();
  }
}