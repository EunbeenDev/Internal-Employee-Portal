document.addEventListener("DOMContentLoaded", () => {
    const errorMessage = document.getElementById("errorMessage");
    const infoMessage = document.getElementById("infoMessage");
    const refreshButton = document.getElementById("refreshButton");
    const logoutButton = document.getElementById("logoutButton");
    const requestCheckButton = document.getElementById("requestCheckButton");
    const terminateEmployeeButton = document.getElementById("terminateEmployeeButton");

    const employeeInfoEmptyMessage = document.getElementById("employeeInfoEmptyMessage");
    const employeeInfoSection = document.getElementById("employeeInfoSection");

    const employeeCodeValue = document.getElementById("employeeCodeValue");
    const employeeEmailValue = document.getElementById("employeeEmailValue");
    const employeeNameValue = document.getElementById("employeeNameValue");
    const employeeDateOfBirthValue = document.getElementById("employeeDateOfBirthValue");

    const statusEmptyMessage = document.getElementById("statusEmptyMessage");
    const statusSection = document.getElementById("statusSection");
    const resultOnlyFields = document.querySelectorAll(".result-only-field");

    const statusCheckId = document.getElementById("statusCheckId");
    const statusValue = document.getElementById("statusValue");
    const statusCreatedAt = document.getElementById("statusCreatedAt");
    const statusCompletedAt = document.getElementById("statusCompletedAt");
    const statusCriminalRecord = document.getElementById("statusCriminalRecord");
    const statusEducationVerified = document.getElementById("statusEducationVerified");
    const statusEmploymentVerified = document.getElementById("statusEmploymentVerified");
    const statusCreditScore = document.getElementById("statusCreditScore");

    const clearHistoryTableBody = document.getElementById("clearHistoryTableBody");
    const flaggedHistoryTableBody = document.getElementById("flaggedHistoryTableBody");

    const params = new URLSearchParams(window.location.search);
    const employeeCode = params.get("employeeCode");

    function getAccessToken() {
        return localStorage.getItem("accessToken");
    }

    function clearMessages() {
        errorMessage.textContent = "";
        infoMessage.textContent = "";
    }

    function showError(message) {
        errorMessage.textContent = message;
        infoMessage.textContent = "";
    }

    function showInfo(message) {
        infoMessage.textContent = message;
        errorMessage.textContent = "";
    }

    function requireLogin() {
        const token = getAccessToken();
        if (!token) {
            window.location.href = "/html/login.html";
            return false;
        }
        return true;
    }

    function buildAuthHeaders(includeContentType = true) {
        const headers = {
            "Authorization": `Bearer ${getAccessToken()}`
        };

        if (includeContentType) {
            headers["Content-Type"] = "application/json";
        }

        return headers;
    }

    function handleUnauthorized(response) {
        if (response.status === 401 || response.status === 403) {
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");
            showError("인증이 만료되었거나 관리자 권한이 없습니다. 다시 로그인해주세요.");
            setTimeout(() => {
                window.location.href = "/html/login.html";
            }, 1000);
            return true;
        }
        return false;
    }

    function formatDateTime(value) {
        if (!value) return "-";
        const date = new Date(value);
        if (Number.isNaN(date.getTime())) return value;
        return date.toLocaleString("ko-KR");
    }

    function formatBoolean(value) {
        if (value === true) return "true";
        if (value === false) return "false";
        return "-";
    }

    function escapeHtml(value) {
        if (value === null || value === undefined) return "-";
        return String(value)
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#39;");
    }

    function setInitialState() {
        employeeCodeValue.textContent = employeeCode || "-";
        employeeEmailValue.textContent = "-";
        employeeNameValue.textContent = "-";
        employeeDateOfBirthValue.textContent = "-";

        employeeInfoSection.style.display = "none";
        employeeInfoEmptyMessage.style.display = "block";
        employeeInfoEmptyMessage.textContent = "직원 상세 정보를 불러오는 중입니다.";

        statusSection.style.display = "none";
        statusEmptyMessage.style.display = "block";
        statusEmptyMessage.textContent = "최신 background check 결과를 불러오는 중입니다.";
    }

    function renderEmployeeInfo(data) {
        if (!data || typeof data !== "object") {
            employeeInfoSection.style.display = "none";
            employeeInfoEmptyMessage.style.display = "block";
            employeeInfoEmptyMessage.textContent = "조회 가능한 직원 상세 정보가 없습니다.";
            return;
        }

        employeeInfoSection.style.display = "grid";
        employeeInfoEmptyMessage.style.display = "none";

        employeeCodeValue.textContent = employeeCode ?? "-";
        employeeEmailValue.textContent = data.email ?? "-";
        employeeNameValue.textContent = data.employeeName ?? "-";
        employeeDateOfBirthValue.textContent = data.dateOfBirth ?? "-";
    }

    function renderStatus(data) {
        if (!data || typeof data !== "object") {
            statusSection.style.display = "none";
            statusEmptyMessage.style.display = "block";
            statusEmptyMessage.textContent = "조회 가능한 background check 결과가 없습니다.";
            return;
        }

        statusSection.style.display = "grid";
        statusEmptyMessage.style.display = "none";

        statusCheckId.textContent = data.checkId ?? "-";
        statusCreatedAt.textContent = formatDateTime(data.createdAt);

        const statusRaw = (data.status ?? "-").toString().toLowerCase();
        statusValue.innerHTML = `<span class="status-badge ${escapeHtml(statusRaw)}">${escapeHtml(statusRaw)}</span>`;

        const isPending = statusRaw === "pending";

        resultOnlyFields.forEach(field => {
            field.style.display = isPending ? "none" : "flex";
        });

        if (isPending) {
            statusCompletedAt.textContent = "-";
            statusCriminalRecord.textContent = "-";
            statusEducationVerified.textContent = "-";
            statusEmploymentVerified.textContent = "-";
            statusCreditScore.textContent = "-";
            return;
        }

        statusCompletedAt.textContent = formatDateTime(data.completedAt);
        statusCriminalRecord.textContent = formatBoolean(data.criminalRecord);
        statusEducationVerified.textContent = formatBoolean(data.educationVerified);
        statusEmploymentVerified.textContent = formatBoolean(data.employmentVerified);
        statusCreditScore.textContent = data.creditScore ?? "-";
    }

    function renderHistoryTable(tableBody, items, emptyMessage, statusClassName) {
        if (!Array.isArray(items) || items.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="7" class="empty-cell">${emptyMessage}</td>
                </tr>
            `;
            return;
        }

        tableBody.innerHTML = items.map(item => {
            const statusRaw = (item.status ?? "-").toString().toLowerCase();

            return `
                <tr>
                    <td>${escapeHtml(item.checkId ?? "-")}</td>
                    <td>${escapeHtml(item.employeeId ?? "-")}</td>
                    <td>${escapeHtml(item.employeeName ?? "-")}</td>
                    <td><span class="status-badge ${escapeHtml(statusClassName || statusRaw)}">${escapeHtml(statusRaw)}</span></td>
                    <td>${escapeHtml(item.creditScore ?? "-")}</td>
                    <td>${escapeHtml(formatDateTime(item.createdAt))}</td>
                    <td>${escapeHtml(formatDateTime(item.completedAt))}</td>
                </tr>
            `;
        }).join("");
    }

    async function parseJsonIfExists(response) {
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            return response.json();
        }
        return null;
    }

    async function fetchLatestBackgroundCheck() {
        const response = await fetch(`/admin/background-checks/${encodeURIComponent(employeeCode)}`, {
            method: "GET",
            headers: buildAuthHeaders()
        });

        if (handleUnauthorized(response)) {
            return null;
        }

        const data = await parseJsonIfExists(response);

        if (!response.ok) {
            throw new Error(
                data?.message ||
                data?.error ||
                "직원 상세 정보를 불러오지 못했습니다."
            );
        }

        return data;
    }

    async function fetchClearHistory() {
        const response = await fetch(`/admin/background-checks/${encodeURIComponent(employeeCode)}/list/clear`, {
            method: "GET",
            headers: buildAuthHeaders()
        });

        if (handleUnauthorized(response)) {
            return null;
        }

        const data = await parseJsonIfExists(response);

        if (!response.ok) {
            throw new Error(
                data?.message ||
                data?.error ||
                "clear 히스토리를 불러오지 못했습니다."
            );
        }

        return data;
    }

    async function fetchFlaggedHistory() {
        const response = await fetch(`/admin/background-checks/${encodeURIComponent(employeeCode)}/list/flagged`, {
            method: "GET",
            headers: buildAuthHeaders()
        });

        if (handleUnauthorized(response)) {
            return null;
        }

        const data = await parseJsonIfExists(response);

        if (!response.ok) {
            throw new Error(
                data?.message ||
                data?.error ||
                "flagged 히스토리를 불러오지 못했습니다."
            );
        }

        return data;
    }

    async function requestBackgroundCheck() {
        clearMessages();

        if (!employeeCode) {
            showError("직원 코드가 없어 background check를 요청할 수 없습니다.");
            return;
        }

        requestCheckButton.disabled = true;
        requestCheckButton.textContent = "요청 중...";

        try {
            const response = await fetch(`/admin/background-checks/${encodeURIComponent(employeeCode)}`, {
                method: "POST",
                headers: buildAuthHeaders(false)
            });

            if (handleUnauthorized(response)) {
                return;
            }

            const data = await parseJsonIfExists(response);

            if (!response.ok) {
                throw new Error(
                    data?.message ||
                    data?.error ||
                    "background check 요청에 실패했습니다."
                );
            }

            showInfo(data?.message || "background check 요청이 완료되었습니다.");
            await loadPageData();
        } catch (error) {
            console.error("background check 요청 실패:", error);
            showError(error.message || "background check 요청 중 오류가 발생했습니다.");
        } finally {
            requestCheckButton.disabled = false;
            requestCheckButton.textContent = "Background Check 요청";
        }
    }

    async function requestEmployeeTermination() {
        clearMessages();

        if (!employeeCode) {
            showError("직원 코드가 없어 퇴사 요청을 처리할 수 없습니다.");
            return;
        }

        const confirmed = window.confirm("해당 직원의 퇴사를 요청하시겠습니까?");
        if (!confirmed) {
            return;
        }

        terminateEmployeeButton.disabled = true;
        terminateEmployeeButton.textContent = "요청 중...";

        try {
            const response = await fetch(`/admin/users/termination/${encodeURIComponent(employeeCode)}`, {
                method: "POST",
                headers: buildAuthHeaders(false)
            });

            if (handleUnauthorized(response)) {
                return;
            }

            const data = await parseJsonIfExists(response);

            if (!response.ok) {
                throw new Error(
                    data?.message ||
                    data?.error ||
                    "퇴사 요청에 실패했습니다."
                );
            }

            showInfo(data?.message || "퇴사 요청이 완료되었습니다.");
            await loadPageData();
        } catch (error) {
            console.error("퇴사 요청 실패:", error);
            showError(error.message || "퇴사 요청 중 오류가 발생했습니다.");
        } finally {
            terminateEmployeeButton.disabled = false;
            terminateEmployeeButton.textContent = "직원 퇴사 요청";
        }
    }

    async function logout() {
        clearMessages();

        try {
            const response = await fetch("/auth/logout", {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${getAccessToken()}`
                }
            });

            if (response.ok || response.status === 401 || response.status === 403) {
                localStorage.removeItem("accessToken");
                localStorage.removeItem("refreshToken");
                window.location.href = "/html/login.html";
                return;
            }

            const data = await parseJsonIfExists(response);
            showError(data?.message || data?.error || "로그아웃에 실패했습니다.");
        } catch (error) {
            console.error("로그아웃 실패:", error);
            showError("로그아웃 중 오류가 발생했습니다.");
        }
    }

    async function loadPageData() {
        clearMessages();

        if (!employeeCode) {
            showError("employeeCode가 없어 상세 페이지를 조회할 수 없습니다.");
            employeeInfoSection.style.display = "none";
            employeeInfoEmptyMessage.style.display = "block";
            employeeInfoEmptyMessage.textContent = "직원 코드가 필요합니다.";

            statusSection.style.display = "none";
            statusEmptyMessage.style.display = "block";
            statusEmptyMessage.textContent = "직원 코드가 필요합니다.";

            clearHistoryTableBody.innerHTML = `<tr><td colspan="7" class="empty-cell">직원 코드가 없어 조회할 수 없습니다.</td></tr>`;
            flaggedHistoryTableBody.innerHTML = `<tr><td colspan="7" class="empty-cell">직원 코드가 없어 조회할 수 없습니다.</td></tr>`;
            return;
        }

        setInitialState();

        clearHistoryTableBody.innerHTML = `<tr><td colspan="7" class="empty-cell">데이터를 불러오는 중입니다.</td></tr>`;
        flaggedHistoryTableBody.innerHTML = `<tr><td colspan="7" class="empty-cell">데이터를 불러오는 중입니다.</td></tr>`;

        try {
            const [latest, clearHistory, flaggedHistory] = await Promise.all([
                fetchLatestBackgroundCheck(),
                fetchClearHistory(),
                fetchFlaggedHistory()
            ]);

            if (latest) {
                renderEmployeeInfo(latest);
                renderStatus(latest);
            } else {
                renderEmployeeInfo(null);
                renderStatus(null);
            }

            if (clearHistory) {
                renderHistoryTable(
                    clearHistoryTableBody,
                    clearHistory,
                    "clear 상태 히스토리가 없습니다.",
                    "clear"
                );
            }

            if (flaggedHistory) {
                renderHistoryTable(
                    flaggedHistoryTableBody,
                    flaggedHistory,
                    "flagged 상태 히스토리가 없습니다.",
                    "flagged"
                );
            }

            showInfo("직원 상세 정보를 불러왔습니다.");
        } catch (error) {
            console.error("직원 상세 조회 실패:", error);
            showError(error.message || "직원 상세 조회 중 오류가 발생했습니다.");
            renderEmployeeInfo(null);
            renderStatus(null);
            clearHistoryTableBody.innerHTML = `<tr><td colspan="7" class="empty-cell">clear 히스토리를 불러오지 못했습니다.</td></tr>`;
            flaggedHistoryTableBody.innerHTML = `<tr><td colspan="7" class="empty-cell">flagged 히스토리를 불러오지 못했습니다.</td></tr>`;
        }
    }

    if (!requireLogin()) {
        return;
    }

    refreshButton.addEventListener("click", loadPageData);
    logoutButton.addEventListener("click", logout);
    requestCheckButton.addEventListener("click", requestBackgroundCheck);
    if (terminateEmployeeButton) {
        terminateEmployeeButton.addEventListener("click", requestEmployeeTermination);
    }

    loadPageData();
});