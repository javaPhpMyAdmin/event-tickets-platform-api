package com.batista.tickets.controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.batista.tickets.domain.CreateEventRequest;
import com.batista.tickets.domain.dtos.CreateEventRequestDTO;
import com.batista.tickets.domain.dtos.CreateEventResponseDTO;
import com.batista.tickets.domain.entities.Event;
import com.batista.tickets.mappers.EventMapper;
import com.batista.tickets.services.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

  private final EventMapper eventMapper;
  private final EventService eventService;

  @GetMapping()
  public Map<String, String> getMethodName() {
    return Map.of("message", "Test get method from container");
  }

  @PostMapping()
  public ResponseEntity<CreateEventResponseDTO> createEvent(
      @Valid @RequestBody CreateEventRequestDTO createEventRequestDTO,
      @AuthenticationPrincipal Jwt jwt) {

    CreateEventRequest createEventRequest = eventMapper.fromDTO(createEventRequestDTO);
    UUID userId = UUID.fromString(jwt.getSubject());

    Event createdEvent = eventService.createEvent(userId, createEventRequest);
    CreateEventResponseDTO createEventResponseDTO = eventMapper.toDTO(createdEvent);

    return new ResponseEntity<>(createEventResponseDTO, HttpStatus.CREATED);
  }

}
