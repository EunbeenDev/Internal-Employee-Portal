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

    @Column(name = "vendor_check_id", nullable = false)
    private String vendorCheckId;

    @Column(name = "completed_at", nullable = false)
    private String completedAt;

    @Column(name = "requested_at", nullable = false)
    private String requestedAt;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "check_status")
    private CheckStatus checkStatus = CheckStatus.PENDING;

    @Builder
    public BackgroundCheck(Employee employee, String vendorCheckId, String completedAt, String requestedAt) {
        this.employee = employee;
        this.vendorCheckId = vendorCheckId;
        this.completedAt = completedAt;
        this.requestedAt = requestedAt;
    }

}
