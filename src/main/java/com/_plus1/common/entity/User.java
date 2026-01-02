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

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private boolean isDeleted = false;

    public User(String email, String username, String password, String nickname, String phoneNumber, boolean isDeleted) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.isDeleted = isDeleted;
    }
}
