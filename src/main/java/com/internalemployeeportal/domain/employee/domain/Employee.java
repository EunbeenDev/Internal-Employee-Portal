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

    @Column(name = "employee_code", nullable = true)
    private String employeeCode;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "date_of_birth", nullable = false)
    private String dateOfBirth;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = true)
    private Department department;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "employee_status")
    private EmployeeStatus employeeStatus = EmployeeStatus.EMPLOYED;


    @Builder
    public Employee(String firstName, String lastName, String email, String dateOfBirth, Department department) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.department = department;
    }

    public void updateEmployeeStatus(EmployeeStatus employeeStatus) {
        this.employeeStatus = employeeStatus;
    }

    public void updateEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public void updateDepartment(Department existingDepartment) {
        	this.department = existingDepartment;
    }

    public void updateEmployeeInfo(String firstName, String lastName, String email, String dateOfBirth) {
        if (firstName != null) {
            this.firstName = firstName;
        }
        if (lastName != null) {
            this.lastName = lastName;
        }
        if (email != null) {
            this.email = email;
        }
        if (dateOfBirth != null) {
            this.dateOfBirth = dateOfBirth;
        }
    }
}
