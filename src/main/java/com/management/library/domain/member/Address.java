package com.management.library.domain.member;

import static com.management.library.service.member.dto.MemberServiceCreateDto.Request;

import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Address {

  private String legion;
  private String city;
  private String street;

  @Builder
  public Address(String legion, String city, String street) {
    this.legion = legion;
    this.city = city;
    this.street = street;
  }

  public static Address of(Request request){
    return Address.builder()
        .legion(request.getLegion())
        .city(request.getCity())
        .street(request.getStreet())
        .build();
  }

  public static Address of(String legion, String city, String street){
    return Address.builder()
        .legion(legion)
        .city(city)
        .street(street)
        .build();
  }
}
