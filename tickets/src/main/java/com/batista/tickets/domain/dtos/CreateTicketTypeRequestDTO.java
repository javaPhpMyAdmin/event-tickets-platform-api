package com.batista.tickets.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTicketTypeRequestDTO {

  @NotBlank(message = "Ticket type name is required")
  private String name;

  @NotNull(message = "Ticket type price is required")
  @PositiveOrZero(message = "Price must bee zero or grater")
  private Double price;

  @Size(max = 150, message = "Description only can contain 150 characters")
  private String description;

  private Integer totalAvailable;

}
