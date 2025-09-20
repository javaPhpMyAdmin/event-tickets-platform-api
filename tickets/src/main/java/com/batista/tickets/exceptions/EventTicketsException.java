package com.batista.tickets.exceptions;

public class EventTicketsException extends RuntimeException {
  public EventTicketsException() {
    super();
  }

  public EventTicketsException(String message) {
    super(message);
  }

  public EventTicketsException(String message, Throwable cause) {
    super(message, cause);
  }

  public EventTicketsException(Throwable cause) {
    super(cause);
  }
}
