package com.management.library.controller.admin;

import static com.management.library.controller.admin.dto.AdminCreateControllerDto.Request;
import static com.management.library.controller.admin.dto.AdminCreateControllerDto.Response;

import com.management.library.controller.book.dto.BookControllerCreateDto;
import com.management.library.service.admin.AdminService;
import com.management.library.service.admin.dto.AdminCreateServiceDto;
import com.management.library.service.book.BookService;
import com.management.library.service.book.dto.BookServiceCreateDto;
import com.management.library.service.member.MemberService;
import com.management.library.service.rental.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자 기능 사용 컨트롤러
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins")
public class AdminController {

  private final AdminService adminService;
  private final RentalService rentalService;
  private final MemberService memberService;
  private final BookService bookService;

  @PostMapping
  public Response createAdmin(@RequestBody Request request) {
    AdminCreateServiceDto.Response admin = adminService.createAdmin(
        AdminCreateServiceDto.Request.of(request));

    return Response.of(admin);
  }

  @PostMapping
  public BookControllerCreateDto.Response createBook(
      @RequestBody BookControllerCreateDto.Request request
  ) {
    BookServiceCreateDto.Response newBook = bookService.createNewBook(
        BookServiceCreateDto.Request.of(request));

    return BookControllerCreateDto.Response.of(newBook);
  }
}
