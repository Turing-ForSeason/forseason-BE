package com.turing.forseason.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Admin")
public class AdminEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private int adminId;

    @Column(name = "admin_email", nullable = false, length = 50)
    private String adminEmail;

    @Column(name = "admin_password", nullable = false, length = 30)
    private String adminPassword;

}
