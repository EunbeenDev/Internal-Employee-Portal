package com.internalemployeeportal.domain.user.domain;


import com.internalemployeeportal.domain.common.BaseEntity;
import com.internalemployeeportal.domain.employee.domain.Employee;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="User")
@NoArgsConstructor
@Getter
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id", updatable = false, nullable = false, unique = true)
    private Long userId;

    @Column(name = "account_id", nullable = false, unique = true)
    private String accountId;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.EMPLOYEE;

    @OneToOne
    @JoinColumn(name="employee_id", nullable = true)
    private Employee employee;

    @Builder
    public User(String accountId, String passwordHash, Role role, Employee employee) {
        this.accountId = accountId;
        this.passwordHash = passwordHash;
        this.role = role;
        this.employee = null;
    }

}