package com.batista.tickets.services;

import java.util.UUID;

import com.batista.tickets.domain.CreateEventRequest;
import com.batista.tickets.domain.entities.Event;

public interface EventService {

  Event createEvent(UUID userId, CreateEventRequest event);

}
