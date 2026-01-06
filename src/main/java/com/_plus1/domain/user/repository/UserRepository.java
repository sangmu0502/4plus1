package com._plus1.domain.user.repository;

import com._plus1.common.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailAndIsDeletedFalse(String email);

    Optional<User> findByNicknameAndIsDeletedFalse(String nickname);

    Optional<User> findByIdAndIsDeletedFalse(Long userId);

    boolean existsByNickname(String nickname);

    boolean existsByPhoneNumber(String phoneNumber);
}
