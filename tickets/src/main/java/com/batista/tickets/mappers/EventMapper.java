package com.batista.tickets.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.batista.tickets.domain.CreateEventRequest;
import com.batista.tickets.domain.CreateTicketTypeRequest;
import com.batista.tickets.domain.dtos.CreateEventRequestDTO;
import com.batista.tickets.domain.dtos.CreateEventResponseDTO;
import com.batista.tickets.domain.dtos.CreateTicketTypeRequestDTO;
import com.batista.tickets.domain.entities.Event;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

  CreateTicketTypeRequest fromDTO(CreateTicketTypeRequestDTO dto);

  CreateEventRequest fromDTO(CreateEventRequestDTO dto);

  CreateEventResponseDTO toDTO(Event event);
}
