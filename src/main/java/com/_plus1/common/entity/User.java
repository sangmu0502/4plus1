package com._plus1.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private boolean isDeleted = false;

    public User(String email, String username, String password, String nickname, String phoneNumber) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
    }

    // 프로필 업데이트
    public void update(String username, String email, String nickname, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
    }
}
