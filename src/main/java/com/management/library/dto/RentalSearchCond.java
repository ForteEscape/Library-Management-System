package com.management.library.dto;

import com.management.library.domain.type.RentalStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalSearchCond {

  private RentalStatus rentalStatus;
}
