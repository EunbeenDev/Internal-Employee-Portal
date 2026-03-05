package com.internalemployeeportal.domain.department.domain;


import com.internalemployeeportal.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Department")
@NoArgsConstructor
@Getter
public class Department extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="department_id", updatable = false, nullable = false, unique = true)
    private Long departmentId;

    @Column(name = "department_name", nullable = false, unique = true)
    private String departmentName;

    @Builder
    public Department(String departmentName) {
        this.departmentName = departmentName;
    }
}
