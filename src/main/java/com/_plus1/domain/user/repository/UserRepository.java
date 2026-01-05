package com._plus1.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com._plus1.common.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
