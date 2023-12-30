package dev.kpuda.persistence.repository;

import dev.kpuda.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}