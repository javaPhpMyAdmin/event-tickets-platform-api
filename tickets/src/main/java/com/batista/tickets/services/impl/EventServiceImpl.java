package com.batista.tickets.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.batista.tickets.domain.CreateEventRequest;
import com.batista.tickets.domain.entities.Event;
import com.batista.tickets.domain.entities.TicketType;
import com.batista.tickets.domain.entities.User;
import com.batista.tickets.repositories.EventRepository;
import com.batista.tickets.repositories.UserRepository;
import com.batista.tickets.services.EventService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

  private final UserRepository userRepository;
  private final EventRepository eventRepository;

  @Override
  public Event createEvent(UUID organizerId, CreateEventRequest event) {
    User organizer = userRepository.findById(organizerId)
        .orElseThrow(
            () -> new UsernameNotFoundException(String.format("Organizer with ID '%s' not found", organizerId)));
    // CREATE A NEW EVENT INSTANCE
    Event eventToCreate = new Event();

    List<TicketType> ticketTypesToCreate = event.getTicketTypes().stream().map(ticketType -> {
      TicketType ticketTypeToCreate = new TicketType();
      ticketTypeToCreate.setName(ticketType.getName());
      ticketTypeToCreate.setPrice(ticketType.getPrice());
      ticketTypeToCreate.setDescription(ticketType.getDescription());
      ticketTypeToCreate.setTotalAvailable(ticketType.getTotalAvailable());
      ticketTypeToCreate.setEvent(eventToCreate);
      return ticketTypeToCreate;
    }).toList();

    eventToCreate.setName(event.getName());
    eventToCreate.setStart(event.getStart());
    eventToCreate.setEnd(event.getEnd());
    eventToCreate.setVenue(event.getVenue());
    eventToCreate.setSalesStart(event.getSalesStart());
    eventToCreate.setSalesEnd(event.getSalesEnd());
    eventToCreate.setStatus(event.getStatus());
    eventToCreate.setOrganizer(organizer);
    eventToCreate.setTicketTypes(ticketTypesToCreate);

    return eventRepository.save(eventToCreate);

  }

}
