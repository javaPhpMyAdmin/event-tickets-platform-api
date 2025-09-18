package com.batista.tickets.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.batista.tickets.domain.User;

public interface UserRepository extends JpaRepository<User, UUID> {

}
