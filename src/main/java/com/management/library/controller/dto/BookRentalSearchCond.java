package com.management.library.controller.dto;

import com.management.library.domain.type.RentalStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookRentalSearchCond {

  private RentalStatus rentalStatus;
}
