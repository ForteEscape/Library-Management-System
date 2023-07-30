package com.management.library.service.rental;

import static com.management.library.exception.ErrorCode.BOOK_RENTAL_COUNT_EXCEED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.management.library.AbstractContainerBaseTest;
import com.management.library.exception.RentalException;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RentalRedisServiceTest extends AbstractContainerBaseTest {

  @Autowired
  private RentalRedisService redisService;
  @Autowired
  private RedisTemplate<String, String> redisTemplate;
  private static final String RENTAL_REDIS_KEY = "rental-count";
  private static final String PENALTY_MEMBER_KEY = "penalty:";

  @AfterEach
  void tearDown() {
    redisTemplate.delete(RENTAL_REDIS_KEY);
    redisTemplate.delete(PENALTY_MEMBER_KEY + "100000001");
  }

  @DisplayName("도서를 대여한 경우, 도서 대여 가능 수를 1 깎는다.")
  @Test
  public void checkMemberRentalBookCount() throws Exception {
    // given
    String memberCode = "10000001";

    // when
    redisService.checkMemberRentalBookCount(memberCode);
    String remainCount = String.valueOf(
        redisTemplate.opsForHash().get(RENTAL_REDIS_KEY, memberCode));

    // then
    assertThat(remainCount).isEqualTo("1");
  }

  @DisplayName("남은 도서 대여 가능 횟수가 0 이하인 경우 예외를 발생시킨다.")
  @Test
  public void checkMemberRentalBookCountUnderZero() throws Exception {
    // given
    String memberCode = "10000001";
    redisService.checkMemberRentalBookCount(memberCode);
    redisService.checkMemberRentalBookCount(memberCode);

    // when
    // then
    assertThatThrownBy(() -> redisService.checkMemberRentalBookCount(memberCode))
        .isInstanceOf(RentalException.class)
        .extracting("errorCode", "description")
        .contains(
            BOOK_RENTAL_COUNT_EXCEED, BOOK_RENTAL_COUNT_EXCEED.getDescription()
        );
  }

  @DisplayName("하나만 연체되었을 시 대여 불가 캐시가 생성되어있어야 한다.")
  @Test
  public void addMemberOverdueData() throws Exception {
    // given
    String memberCode = "100000001";
    int overdueDays = 3;
    LocalDate penaltyEndDate = LocalDate.now().plusDays(overdueDays);

    // when
    redisService.addMemberOverdueData(memberCode, overdueDays, penaltyEndDate);

    // then
    Long expireDate = redisTemplate.getExpire(PENALTY_MEMBER_KEY + memberCode, TimeUnit.DAYS);
    String value = redisTemplate.opsForValue().get(PENALTY_MEMBER_KEY + memberCode);

    assertThat(value).isEqualTo(penaltyEndDate.toString());
    assertThat(expireDate).isEqualTo(2L);
  }

  @DisplayName("연체 패널티 기간 도중 다시 연체될 시 다시 연체된 기간만큼 패널티 기간이 늘어난다.")
  @Test
  public void addMemberOverdueDataTwice() throws Exception {
    // given
    String memberCode = "100000001";

    int overdueDays1 = 5;
    int overdueDays2 = 3;

    LocalDate penaltyEndDate1 = LocalDate.now().plusDays(overdueDays1);
    LocalDate penaltyEndDate2 = LocalDate.now().plusDays(overdueDays2);

    // when
    redisService.addMemberOverdueData(memberCode, overdueDays1, penaltyEndDate1);
    redisService.addMemberOverdueData(memberCode, overdueDays2, penaltyEndDate2);

    // then
    Long expireDate = redisTemplate.getExpire(PENALTY_MEMBER_KEY + memberCode, TimeUnit.DAYS);
    String value = redisTemplate.opsForValue().get(PENALTY_MEMBER_KEY + memberCode);

    assertThat(value).isEqualTo(penaltyEndDate1.plusDays(overdueDays2).toString());
    assertThat(expireDate).isEqualTo(7L);
  }

  @DisplayName("줄어든 회원 도서 대여 가능 횟수를 다시 증가시킬 수 있다.")
  @Test
  public void addMemberRentalBookCount() throws Exception {
    // given
    String memberCode = "100000001";

    redisService.checkMemberRentalBookCount(memberCode);
    redisService.checkMemberRentalBookCount(memberCode);
    // when
    redisService.addMemberRentalBookCount(memberCode);

    // then
    String rentalCount = String.valueOf(
        redisTemplate.opsForHash().get(RENTAL_REDIS_KEY, memberCode));

    assertThat(rentalCount).isEqualTo("1");
  }
}