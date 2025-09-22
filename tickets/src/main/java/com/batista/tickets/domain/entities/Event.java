package com.batista.tickets.domain.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "event_start")
  private LocalDateTime start;

  @Column(name = "event_end")
  private LocalDateTime end;

  @Column(name = "venue", nullable = false)
  private String venue;

  @Column(name = "sales_start")
  private LocalDateTime salesStart;

  @Column(name = "sales_end")
  private LocalDateTime salesEnd;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private EventStatusEnum status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organizer_id")
  private User organizer;

  @ManyToMany(mappedBy = "attendingEvents")
  @Builder.Default
  private List<User> attendees = new ArrayList<>();

  @ManyToMany(mappedBy = "staffingEvents")
  @Builder.Default
  private List<User> staff = new ArrayList<>();

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
  @Builder.Default
  private List<TicketType> ticketTypes = new ArrayList<>();

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((start == null) ? 0 : start.hashCode());
    result = prime * result + ((end == null) ? 0 : end.hashCode());
    result = prime * result + ((venue == null) ? 0 : venue.hashCode());
    result = prime * result + ((salesStart == null) ? 0 : salesStart.hashCode());
    result = prime * result + ((salesEnd == null) ? 0 : salesEnd.hashCode());
    result = prime * result + ((status == null) ? 0 : status.hashCode());
    result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
    result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Event other = (Event) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (start == null) {
      if (other.start != null)
        return false;
    } else if (!start.equals(other.start))
      return false;
    if (end == null) {
      if (other.end != null)
        return false;
    } else if (!end.equals(other.end))
      return false;
    if (venue == null) {
      if (other.venue != null)
        return false;
    } else if (!venue.equals(other.venue))
      return false;
    if (salesStart == null) {
      if (other.salesStart != null)
        return false;
    } else if (!salesStart.equals(other.salesStart))
      return false;
    if (salesEnd == null) {
      if (other.salesEnd != null)
        return false;
    } else if (!salesEnd.equals(other.salesEnd))
      return false;
    if (status != other.status)
      return false;
    if (createdAt == null) {
      if (other.createdAt != null)
        return false;
    } else if (!createdAt.equals(other.createdAt))
      return false;
    if (updatedAt == null) {
      if (other.updatedAt != null)
        return false;
    } else if (!updatedAt.equals(other.updatedAt))
      return false;
    return true;
  }

  // @Override
  // public boolean equals(Object o) {
  // if (o == null || getClass() != o.getClass())
  // return false;

  // Event event = (Event) o;

  // return Objects.equals(id, event.id) && Objects.equals(name, event.name) &&
  // Objects.equals(start, event.start)
  // && Objects.equals(end, event.end) && Objects.equals(venue, event.venue)
  // && Objects.equals(salesStart, event.salesStart) && Objects.equals(salesEnd,
  // event.salesEnd)
  // && Objects.equals(status, event.status) && Objects.equals(createdAt,
  // event.createdAt)
  // && Objects.equals(updatedAt, event.updatedAt);
  // }

}
