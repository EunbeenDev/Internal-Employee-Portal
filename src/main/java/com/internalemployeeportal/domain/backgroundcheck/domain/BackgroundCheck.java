package com.internalemployeeportal.domain.backgroundcheck.domain;


import com.internalemployeeportal.domain.common.BaseEntity;
import com.internalemployeeportal.domain.employee.domain.Employee;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="BackgroundCheck")
@NoArgsConstructor
@Getter
public class BackgroundCheck extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="background_check_id", updatable = false, nullable = false, unique = true)
    private Long backgroundCheckId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "employee_code", nullable = true)
    private String employeeCode;

    @Column(name = "vendor_check_id", nullable = false)
    private String vendorCheckId;

    @Column(name = "completed_at")
    private String completedAt;

    @Column(name = "requested_at", nullable = false)
    private String requestedAt;

    @Column(name = "criminal_record")
    private Boolean criminalRecord;

    @Column(name = "education_verified")
    private Boolean educationVerified;

    @Column(name = "employment_verified")
    private Boolean employmentVerified;

    @Column(name = "credit_score")
    private String creditScore;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "check_status")
    private CheckStatus checkStatus = CheckStatus.PENDING;

    @Builder
    public BackgroundCheck(Employee employee, String employeeCode, String vendorCheckId, String completedAt, String requestedAt) {
        this.employee = employee;
        this.employeeCode = employeeCode;
        this.vendorCheckId = vendorCheckId;
        this.completedAt = completedAt;
        this.requestedAt = requestedAt;
    }

    public static void updateFromVendorResponse(BackgroundCheck backgroundCheck) {
        backgroundCheck.updateCheckStatus(backgroundCheck.getCheckStatus());
        backgroundCheck.updateCompletedAt(backgroundCheck.getCompletedAt());
        backgroundCheck.criminalRecord = backgroundCheck.isCriminalRecord();
        backgroundCheck.educationVerified = backgroundCheck.isEducationVerified();
        backgroundCheck.employmentVerified = backgroundCheck.isEmploymentVerified();
        backgroundCheck.creditScore = backgroundCheck.getCreditScore();
    }

    private Boolean isCriminalRecord() {
        return this.criminalRecord;
    }

    private Boolean isEducationVerified() {
        return this.educationVerified;
    }

    private Boolean isEmploymentVerified() {
        return this.employmentVerified;
    }

    public void updateCheckStatus(CheckStatus checkStatus) {
        this.checkStatus = checkStatus;
    }

    public void updateCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }
}


