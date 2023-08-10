# Trouble Shooting 문서

- 해당 문서는 개인 프로젝트를 진행하면서 부딪힌 문제들에 대한 해결과 원인에 대해서 기술해놓은 문서입니다.

## 1. 동시성 문제
- 동시성 문제의 경우 프로젝트 내내 여러 곳에서 확인할 수 있던 문제였지만 처음 동시성 문제를 생각한건 회원 가입 부분이었습니다.
- 회원 가입의 경우 관리자가 진행하고 있고, 회원 가입의 결과로 회원 코드와 초기화된 패스워드를 반환하는 구조였습니다.

```java
...

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
  ...
  
  @Transactional
  public MemberCreateServiceDto.Response createMember(MemberCreateServiceDto.Request request){

    if(isPresent(request)){
      throw new DuplicateException(MEMBER_ALREADY_EXISTS);
    }

    // 동시성 문제 발생
    String latestMemberCode = memberRepository.findTopByOrderByIdDesc()
            .orElse("100000000000");

    String memberCode = generateMemberCode(latestMemberCode);
    String password = generatePassword(request.getBirthdayCode);

    Member savedMember = savedMember(request.getName(), request.getBirthdayCode(),
            memberCode, password, address);

    return MemberCreateServiceDto.Response.of(savedMember);
  }

  private Member savedMember(String name, String birthdayCode, String memberCode, String password,
          Address address) {
    Member member = Member.builder()
            .name(name)
            .birthdayCode(birthdayCode)
            .address(address)
            .memberRentalStatus(RENTAL_AVAILABLE)
            .authority(ROLE_MEMBER)
            .memberCode(memberCode)
            .password(password)
            .build();

    return memberRepository.save(member);
  }

  private String generateMemberCode(String latestMemberCode) {
    long generateMemberCodeLong = Long.parseLong(latestMemberCode);
    return String.valueOf(generateMemberCodeLong + 1L);
  }

  private Address getAddress(String legion, String city, String street) {
    return Address.builder()
            .legion(legion)
            .city(city)
            .street(street)
            .build();
  }

  private String generatePassword(String birthdayCode) {
    return birthdayCode + "!@#";
  }

  private boolean isPresent(Request request) {
    return memberRepository.findByMemberNameAndAddress(request.getName(),
            request.getLegion(), request.getCity(), request.getStreet()).isPresent();
  }
}

```
- 처음에는 생성할 회원 코드를 미리 만든 후, 해당 회원 코드가 존재하는지를 확인하여 존재하지 않는 경우 생성할 회원 코드를 사용하는 방식으로 회원 가입을 수행했습니다.
- 이때 여러 명의 사용자(관리자)가 동시에 회원 가입을 수행할 경우 각각의 입장에서 회원 코드가 db에 존재하지 않으므로 생성한 회원 코드를 사용하게 되며 이것이 uk에 위반되어 동시성 문제가 발생했습니다.


- 해결 방법은 초기에는 redisson lock을 사용하여 해결했습니다. 그런데 회원 코드는 회원가입 할 떄마다 1씩 증가하도록 했기 때문에 redis 자체의 증가 연산을 수행하여 해결할 수 있다는 것도 알 수 있었습니다.
- 최종적으로 해결한 방식은 `redisTemplate`의 `increasement()`을 사용하는 방식으로 깔끔하게 구현할 수 있었습니다.

```java
public class RedisMemberService {

  private final RedisTemplate<String, String> redisTemplate;
  private static final String INIT_MEMBER_CODE = "100000000";

  public Long getMemberCode(){
    redisTemplate.opsForValue().setIfAbsent("memberCode", INIT_MEMBER_CODE);

    return redisTemplate.opsForValue().increment("memberCode", 1);
  }
}
```

## 2. 동시성 문제 - 트랜젝션
- 해당 문제는 레디스 락을 사용할 때 발생했던 문제였습니다.
- 현재 프로젝트에서는 레디스 락과 트랜잭션이 같이 들어가는 코드가 사라졌습니다.(redis의 자체 연산을 통해 동시성을 해결했기 때문입니다.)
- 바로 위의 회원 코드 동시성 문제와 이어지는 문제였습니다. 초기에는 redisson을 통한 락을 가져와 테스트를 수행했었습니다.

```java
public class MemberTest{
  
  ...
  
  @DisplayName("동시에 세 회원이 회원 가입을 진행하는 경우 한 명을 제외하고 회원 가입에 실패한다.")
  @Test
  public void createMemberWithConcurrentProblem() throws Exception {
    // given
    ExecutorService executorService = Executors.newFixedThreadPool(3);
    CountDownLatch latch = new CountDownLatch(3);

    Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    Request request2 = createRequest("park", "980101", "경상남도", "김해시", "북부로");
    Request request3 = createRequest("lee", "980101", "경상남도", "김해시", "해반천로");

    // when
    Future<Boolean> submit1 = executorService.submit(() -> {
      try {
        memberService.createMember(request1);
        return true;
      } catch (DuplicateException e) {
        return false;
      } finally {
        latch.countDown();
      }
    });

    Future<Boolean> submit2 = executorService.submit(() -> {
      try {
        memberService.createMember(request2);
        return true;
      } catch (DuplicateException e) {
        return false;
      } finally {
        latch.countDown();
      }
    });

    Future<Boolean> submit3 = executorService.submit(() -> {
      try {
        memberService.createMember(request3);
        return true;
      } catch (DuplicateException e) {
        return false;
      } finally {
        latch.countDown();
      }
    });
    latch.await();

    List<Boolean> result = List.of(submit1.get(), submit2.get(), submit3.get());

    // then
    assertThat(result)
        .hasSize(3)
        .contains(true, false, false);
  }
}
```
- 레디스 락을 사용하여 문제를 해결한 줄 알았는데 테스트 코드 수행시에는 동일하게 uk 위반 예외가 올라오고 있었습니다.

### 2.1 문제 분석
- 로그를 분석하면서 확인한 결과 다음과 같은 동작이 수행되고 있었습니다.
    - `createMember()`에서 트랜잭션이 걸려 있다.
    - 위에서 가장 먼저 수행된 스레드가 락을 얻은 뒤 회원 생성 로직을 진행한다.
    - 트랜잭션이 종료되고 커밋된다 <- 문제점
    - 다른 스레드는 자신이 가져온 회원 코드가 이미 존재하여 회원가입을 진행하지 못하고 예외가 발생된다.

- 문제는 트랜잭션이 정상적으로 commit 되기 이전에 다른 스레드에서 `createMember()`를 수행하는 것이었습니다.
  - 다른 스레드에선 아직 트랜잭션이 정상적으로 commit 되지 않았기 때문에 자신이 가진 회원 코드가 아직 유효하다고 판된되고 이로 인해 회원 등록을 수행합니다.
  - 하지만 DB에 적용되기 이전 어느 시점에서 이전에 수행된 트랜잭션이 commit 되어 DB에 다른 스레드에서 생성 중인 회원의 코드가 이미 등록되어 있게 됩니다.
  - 따라서 DB에 회원을 저장하는 순간 uk인 회원 코드가 중복되는 상황이 발생하여 예외가 발생합니다.

### 2.2 문제 해결
- 문제점을 알았기 때문에 해결책 역시 도출이 가능했습니다.
- 회원 등록 트랜잭션을 내부에서 따로 생성하여 해당 트랜잭션이 종료된 뒤에 락이 풀리도록 구현하면 해결되는 문제였습니다.
  - 트랜잭션 전파 옵션인 `REQUIRES_NEW` 를 사용하여 내부에서 트랜잭션이 종료되고 나서 락이 풀리도록 구현했습니다.
  - 해당 방식을 사용하면 동시성 문제는 해결이 가능하나 connection pool 에서 이로 인한 커넥션 고갈이 발생할 수 있어 좋지 못하다고 생각했습니다.
  - 이후 redis 의 atomic 한 연산을 사용하여 동시성 문제를 해결했고, 락을 사용하지 않게 됨으로서 해당 문제 역시 더 이상 발생하지 않게 되었습니다.


## 3. 엔티티 반환 문제
- Paging 처리에서 엔티티를 바로 반환하는 방식 자체가 좋지 않다라고 생각했었습니다.
- 특히나 엔티티 자체를 외부로 바로 반환하는 것은 문제가 될 소지가 다분하기 때문에 이를 DTO로 변환하여 반환해야 하는데 이것과 관련하여 Paging된 데이터를 어떻게 DTO로 변환해야 하는가를 고민했었습니다.

### 3-1. 방법 1.
- 방법 1은 쿼리를 통해 얻은 리스트를 스트림으로 바로 DTO 리스트로 변환하는 것이었습니다.
```java

public class BookRepositoryImpl{
  /**
   * 도서의 이름, 저자, 출판사를 사용하여 검색이 가능
   * 검색 결과는 페이징되어 반환
   * @param cond 검색 조건
   * @param pageable 페이징 설정
   * @return 페이징된 검색 결과
   */
  @Override
  public Page<BookDto> bookSearch(BookSearchCond cond, Pageable pageable) {
    List<Book> result = queryFactory.selectFrom(book)
        .where(
            bookNameEq(cond.getBookTitle()),
            bookAuthorEq(cond.getBookAuthor()),
            bookPublisherEq(cond.getPublisherName())
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
    
    List<BookDto> content = result.stream()
            .map(BookDto::of)
            .collect(Collectors.toList());

    JPAQuery<Long> countQuery = queryFactory.select(book.count())
        .from(book)
        .where(
            bookNameEq(cond.getBookTitle()),
            bookAuthorEq(cond.getBookAuthor()),
            bookPublisherEq(cond.getPublisherName())
        );

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }
}
```

- 가장 범용적으로 사용할 수 있으나 만약 엔티티를 직접 반환해야 하는 경우에는 어떻게 할 것인가 라는 고민이 생겼습니다.

### 3-2. 방법 2.
- 두 번째 방법은 Projection을 사용하는 것이었습니다.
```java
public class BookRepositoryImpl{
  /**
   * 도서의 이름, 저자, 출판사를 사용하여 검색이 가능
   * 검색 결과는 페이징되어 반환
   * @param cond 검색 조건
   * @param pageable 페이징 설정
   * @return 페이징된 검색 결과
   */
  @Override
  public Page<BookServiceResponseDto> bookSearch(BookSearchCond cond, Pageable pageable) {
    List<BookServiceResponseDto> result = queryFactory.select(
                    constructor(BookServiceResponseDto.class,
                            book.id,
                            book.bookInfo.title,
                            book.bookInfo.author,
                            book.bookInfo.publisher,
                            book.bookInfo.publishedYear,
                            book.bookInfo.location,
                            book.typeCode,
                            book.bookStatus)
            )
            .from(book)
            .where(
                    bookNameEq(cond.getBookTitle()),
                    bookAuthorEq(cond.getBookAuthor()),
                    bookPublisherEq(cond.getPublisherName())
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    JPAQuery<Long> countQuery = queryFactory.select(book.count())
            .from(book)
            .where(
                    bookNameEq(cond.getBookTitle()),
                    bookAuthorEq(cond.getBookAuthor()),
                    bookPublisherEq(cond.getPublisherName())
            );

    return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
  }
}
```
- Projection을 사용하는 경우, 데이터를 DTO로 즉시 조회가 가능하기 때문에 변환하는 코스트가 줄어들어 방법 1보다 빠르게 결과를 반환할 수 있었습니다.
- 대신 Projection의 경우 fetch join을 사용하면 사용할 수 없고, 역시 DTO를 반환하기 때문에 엔티티 자체를 반환해야 하는 경우라면 어떻게 할 것인가라는 문제를 해결하지 못했습니다.


### 3-3 방법 3.
- 3번째 방법은 페이지 객체에서 데이터를 꺼내어 새롭게 페이지를 만드는 것이었습니다.
- 이 경우 현재 페이지와 전체 페이지, 전체 데이터 개수와 같은 페이지 정보를 담는 객체를 따로 만들고, 데이터를 담는 객체도 따로 만들어 줘야 했습니다.
```java
@Getter
@Setter
public class ResultPage<T> {
  
  T data;
  PageInfo pageInfo;
  
  ResultPage(T data, PageInfo pageInfo){
    this.data = data;
    this.pageInfo = pageInfo;
  }
}

@Getter
@Setter
@NoArgsConstructor
public class PageInfo {
  
  private int currentPage;
  private int pageSize;
  private int totalElement;
  private int totalPages;

  public PageInfo(int currentPage, int pageSize, int totalElement, int totalPages) {
    this.currentPage = currentPage;
    this.pageSize = pageSize;
    this.totalElement = totalElement;
    this.totalPages = totalPages;
  }
}
```

- 위와 같은 객체를 사용하면 다음과 같이 만들 수 있습니다.
```java
@Service
@Transactional(readOnly = true)
public class BookService {
  
  ...
  
  public void getAllBooks(Pageable pageable){
    Page<Book> result = bookRepository.findAll(pageable);

    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
            (int) resultPage.getTotalElements(), resultPage.getTotalPages());
    
    List<BookDto> content = result.getContent().stream()
            .map(BookDto::of)
            .collect(Collectors.toList());
    
    return new ResultPage<BookDto>(content, pageInfo);
  }
  
  ...
}
```
- 해당 방식을 사용할 경우, 엔티티를 필요로 하더라도 그대로 반환시키면 되기 때문에 문제가 없고, DTO로 변환하여 반환도 되기 때문에 가장 이상적으로 해결할 수 있는 방법이라고 생각됩니다.


## 4. EC2 메모리 부족 문제
- 배포 도중에 gradle을 사용하여 빌드를 수행하니 EC2 인스턴스가 무한로딩이 되는 상황이 발생하였습니다.
- 처음에는 권한 문제인가 생각되어 `sudo ./gradlew build -x test` 명령어로 변경해보았지만 마찬가지였습니다.
- 그렇게 찾다가 t2.micro의 렘이 1GB라는 것에 주목하였고, 빌드 도중에 메모리가 모두 사용되어 버리면 무한 로딩이 발생할 수 있겠다는 생각이 들었습니다.

### 해결 방법
- 해결 방식은 디스크 용량을 메모리처럼 사용하는 swap 메모리를 사용하여 해결하였습니다.
- `sudo dd if=/dev/zero of=/swapfile bs=128M count=16` 명령어로 128M 씩 16개의 공간으로 2GB의 디스크 공간을 메모리처럼 사용하도록 하였습니다.
- 이후 `sudo chmod 600 /swapfile`로 스왑 파일에 대한 읽기 및 쓰기 권한을 주었습니다.
- `sudo mkswap /swapfile`로 우분투 상에서 스왑 영역을 설정해준 뒤 스왑 파일을 추가하여 즉시 사용이 가능하도록 하였습니다.
  - 스왑 파일 추가는 `sudo swapon /swapfile` 명령어로 수행했습니다.
- 이후 `sudo swapon -s`를 통해 절차가 성공했는지 확인 후 `etc/fstab` 파일에서 설정을 추가하여 부팅 시 스왑 파일을 활성화하도록 설정했습니다.
  - 설정 추가는 vi로 `/swapfile swap swap defaults 0 0`을 마지막 줄에 추가합니다.
- 이후 `free` 명령어로 메모리가 할당되었는지 확인합니다.
- 메모리 스왑을 수행한 뒤에는 빌드가 문제 없이 잘 수행되었던 것을 확인할 수 있었습니다.