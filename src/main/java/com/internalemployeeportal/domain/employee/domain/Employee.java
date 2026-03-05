package com.internalemployeeportal.domain.employee.domain;

import com.internalemployeeportal.domain.common.BaseEntity;
import com.internalemployeeportal.domain.department.domain.Department;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name="Employee")
@NoArgsConstructor
@Getter
public class Employee extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="employee_id", updatable = false, nullable = false, unique = true)
    private Long employeeId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "date_of_birth", nullable = false)
    private String dateOfBirth;

    @Column(name = "position", nullable = false)
    private String position;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "employee_status")
    private EmployeeStatus employeeStatus = EmployeeStatus.EMPLOYED;


    @Builder
    public Employee(String firstName, String lastName, String email, String dateOfBirth, String position, Department department) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.position = position;
        this.department = department;
    }

}
