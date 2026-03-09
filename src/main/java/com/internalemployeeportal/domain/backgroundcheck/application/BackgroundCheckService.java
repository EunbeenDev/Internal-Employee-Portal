package com.internalemployeeportal.domain.backgroundcheck.application;

import com.internalemployeeportal.domain.backgroundcheck.client.BackgroundCheckClient;
import com.internalemployeeportal.domain.backgroundcheck.domain.BackgroundCheck;
import com.internalemployeeportal.domain.backgroundcheck.domain.CheckStatus;
import com.internalemployeeportal.domain.backgroundcheck.domain.repository.BackgroundCheckRepository;
import com.internalemployeeportal.domain.backgroundcheck.dto.request.BackgroundCheckReq;
import com.internalemployeeportal.domain.backgroundcheck.dto.response.BackgroundCheckCreatedRes;
import com.internalemployeeportal.domain.backgroundcheck.dto.response.BackgroundCheckPendingRes;
import com.internalemployeeportal.domain.backgroundcheck.dto.response.BackgroundCheckResultRes;
import com.internalemployeeportal.domain.backgroundcheck.exception.BackgroundCheckNotFoundException;
import com.internalemployeeportal.domain.employee.domain.Employee;
import com.internalemployeeportal.domain.employee.domain.EmployeeStatus;
import com.internalemployeeportal.domain.employee.domain.repository.EmployeeRepository;
import com.internalemployeeportal.domain.employee.exception.EmployeeNotFoundException;
import com.internalemployeeportal.global.payload.CommonApiResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BackgroundCheckService {

    private final BackgroundCheckClient backgroundCheckClient;
    private final BackgroundCheckRepository backgroundCheckRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public ResponseEntity<?> requestBackgroundCheck(String employeeCode) {

        Employee employee = (Employee) employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(EmployeeNotFoundException::new);

        // 이미 존재하는 Background Check가 PENDING 상태인 경우, 기존 데이터를 삭제
        if (backgroundCheckRepository.existsByEmployee(employee)) {
            BackgroundCheck existingCheck = backgroundCheckRepository.findByEmployee(employee);

            if (existingCheck.getCheckStatus() == CheckStatus.PENDING) {
                backgroundCheckRepository.delete(existingCheck);
                log.info("기존 PENDING 상태의 Background Check가 삭제되었습니다. Employee Code: {}", employeeCode);
            }
        }

        BackgroundCheckReq request = BackgroundCheckReq.builder()
                .employeeId(employee.getEmployeeCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .dateOfBirth(employee.getDateOfBirth())
                .build();

        BackgroundCheckCreatedRes response =
                backgroundCheckClient.createBackgroundCheck(request);

        BackgroundCheck backgroundCheck = BackgroundCheck.builder()
                .employee(employee)
                .vendorCheckId(response.getCheckId())
                .requestedAt(response.getCreatedAt())
                .completedAt(null)
                .employeeCode(employee.getEmployeeCode())
                .build();

        backgroundCheckRepository.save(backgroundCheck);

        return buildOkResponse("Background Check 요청이 성공적으로 처리되었습니다.");
    }

    public ResponseEntity<?> getBackgroundCheckResult(String employeeCode) {
        Employee employee = (Employee) employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(EmployeeNotFoundException::new);

        // 가장 최근에 요청된 Background Check를 조회
        BackgroundCheck backgroundCheck = (BackgroundCheck) backgroundCheckRepository.findTopByEmployeeOrderByRequestedAtDesc(employee)
                .orElseThrow(BackgroundCheckNotFoundException::new);


        if(backgroundCheck.getCheckStatus()==CheckStatus.CLEAR || backgroundCheck.getCheckStatus()==CheckStatus.FLAGGED){
            // 조회 결과 업데이트
            BackgroundCheck.updateFromVendorResponse(backgroundCheck);

            BackgroundCheckResultRes result = BackgroundCheckResultRes.builder()
                    .checkId(backgroundCheck.getVendorCheckId())
                    .employeeId(employee.getEmployeeCode())
                    .employeeName(employee.getFirstName() + " " + employee.getLastName())
                    .email(employee.getEmail())
                    .dateOfBirth(employee.getDateOfBirth())
                    .status(String.valueOf(backgroundCheck.getCheckStatus()))
                    .criminalRecord(backgroundCheck.getCriminalRecord())
                    .educationVerified(backgroundCheck.getEducationVerified())
                    .employmentVerified(backgroundCheck.getEmploymentVerified())
                    .creditScore(backgroundCheck.getCreditScore())
                    .createdAt(backgroundCheck.getRequestedAt())
                    .completedAt(backgroundCheck.getCompletedAt() != null ? backgroundCheck.getCompletedAt() : null)
                    .build();

            return ResponseEntity.ok(result);

        }
        else{
            BackgroundCheckPendingRes pendingRes = BackgroundCheckPendingRes.builder()
                    .checkId(backgroundCheck.getVendorCheckId())
                    .employeeId(employee.getEmployeeCode())
                    .employeeName(employee.getFirstName() + " " + employee.getLastName())
                    .email(employee.getEmail())
                    .dateOfBirth(employee.getDateOfBirth())
                    .status(String.valueOf(backgroundCheck.getCheckStatus()))
                    .createdAt(backgroundCheck.getRequestedAt())
                    .build();

            return ResponseEntity.ok(pendingRes);
        }
    }

    // 모든 Pending 상태의 Background Check 조회
    public ResponseEntity<?> getPendingBackgroundCheckResultList() {
        List<BackgroundCheck> pendingChecks = backgroundCheckRepository.findByCheckStatus(
                CheckStatus.PENDING
        );

        List<BackgroundCheckPendingRes> pendingResList = pendingChecks.stream()
                .map(check -> BackgroundCheckPendingRes.builder()
                        .checkId(check.getVendorCheckId())
                        .employeeId(check.getEmployeeCode())
                        .employeeName(check.getEmployee().getFirstName() + " " + check.getEmployee().getLastName())
                        .dateOfBirth(check.getEmployee().getDateOfBirth())
                        .status(String.valueOf(check.getCheckStatus()))
                        .createdAt(check.getRequestedAt())
                        .isTerminated(check.getEmployee().getEmployeeStatus() == EmployeeStatus.TERMINATED)
                        .build())
                .toList();

        return ResponseEntity.ok(pendingResList);
    }

    public ResponseEntity<?> getClearBackgroundCheckResultList(String employeeCode) {
        List<BackgroundCheck> clearChecks = backgroundCheckRepository.findByEmployeeCodeAndCheckStatus(
                employeeCode, CheckStatus.CLEAR
        );

        List<BackgroundCheckResultRes> clearResList = clearChecks.stream()
                .map(check -> BackgroundCheckResultRes.builder()
                        .checkId(check.getVendorCheckId())
                        .employeeId(check.getEmployeeCode())
                        .employeeName(check.getEmployee().getFirstName() + " " + check.getEmployee().getLastName())
                        .dateOfBirth(check.getEmployee().getDateOfBirth())
                        .status(String.valueOf(check.getCheckStatus()))
                        .criminalRecord(check.getCriminalRecord())
                        .educationVerified(check.getEducationVerified())
                        .employmentVerified(check.getEmploymentVerified())
                        .creditScore(check.getCreditScore())
                        .createdAt(check.getRequestedAt())
                        .completedAt(check.getCompletedAt() != null ? check.getCompletedAt() : null)
                        .build())
                .toList();

        return ResponseEntity.ok(clearResList);
    }

    public ResponseEntity<?> getFlaggedBackgroundCheckResultList(String employeeCode) {
        List<BackgroundCheck> flaggedChecks = backgroundCheckRepository.findByEmployeeCodeAndCheckStatus(
                employeeCode, CheckStatus.FLAGGED
        );

        List<BackgroundCheckResultRes> flaggedResList = flaggedChecks.stream()
                .map(check -> BackgroundCheckResultRes.builder()
                        .checkId(check.getVendorCheckId())
                        .employeeId(check.getEmployeeCode())
                        .employeeName(check.getEmployee().getFirstName() + " " + check.getEmployee().getLastName())
                        .dateOfBirth(check.getEmployee().getDateOfBirth())
                        .status(String.valueOf(check.getCheckStatus()))
                        .criminalRecord(check.getCriminalRecord())
                        .educationVerified(check.getEducationVerified())
                        .employmentVerified(check.getEmploymentVerified())
                        .creditScore(check.getCreditScore())
                        .createdAt(check.getRequestedAt())
                        .completedAt(check.getCompletedAt() != null ? check.getCompletedAt() : null)
                        .build())
                .toList();

        return ResponseEntity.ok(flaggedResList);
    }


    // 응답 빌더 메서드
    private ResponseEntity<?> buildOkResponse(String message) {
        CommonApiResponse<Object> response = CommonApiResponse.builder()
                .check(true)
                .information(message)
                .build();
        return ResponseEntity.ok(response);
    }



}