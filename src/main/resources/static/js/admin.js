document.addEventListener("DOMContentLoaded", () => {
    const employeeCount = document.getElementById("employeeCount");
    const pendingCount = document.getElementById("pendingCount");
    const employeeTableBody = document.getElementById("employeeTableBody");
    const pendingTableBody = document.getElementById("pendingTableBody");
    const refreshButton = document.getElementById("refreshButton");
    const logoutButton = document.getElementById("logoutButton");
    const errorMessage = document.getElementById("errorMessage");
    const infoMessage = document.getElementById("infoMessage");

    function getAccessToken() {
        return localStorage.getItem("accessToken");
    }

    function getRefreshToken() {
        return localStorage.getItem("refreshToken");
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

    function buildAuthHeaders() {
        const token = getAccessToken();
        return {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        };
    }

    function handleUnauthorized(response) {
        if (response.status === 401 || response.status === 403) {
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");
            showError("인증이 만료되었거나 권한이 없습니다. 다시 로그인해주세요.");
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

    function renderEmployees(employees) {
        if (!Array.isArray(employees) || employees.length === 0) {
            employeeTableBody.innerHTML = `
                <tr>
                    <td colspan="4" class="empty-cell">조회된 직원이 없습니다.</td>
                </tr>
            `;
            employeeCount.textContent = "0";
            return;
        }

        employeeCount.textContent = String(employees.length);

        const previewEmployees = employees.slice(0, 5);

        employeeTableBody.innerHTML = previewEmployees.map(employee => `
            <tr>
                <td>${employee.employeeCode ?? "-"}</td>
                <td>${employee.name ?? "-"}</td>
                <td>${employee.department ?? "-"}</td>
                <td>${employee.isTerminated ? "퇴사" : "재직 중"}</td>
            </tr>
        `).join("");
    }

    function renderPendingChecks(pendingChecks) {
        if (!Array.isArray(pendingChecks) || pendingChecks.length === 0) {
            pendingTableBody.innerHTML = `
                <tr>
                    <td colspan="7" class="empty-cell">현재 pending 상태의 background check가 없습니다.</td>
                </tr>
            `;
            pendingCount.textContent = "0";
            return;
        }

        pendingCount.textContent = String(pendingChecks.length);

        const previewPendingChecks = pendingChecks.slice(0, 5);

        pendingTableBody.innerHTML = previewPendingChecks.map(item => `
            <tr>
                <td>${item.checkId ?? "-"}</td>
                <td>${item.employeeId ?? "-"}</td>
                <td>${item.employeeName ?? "-"}</td>
                <td>${item.dateOfBirth ?? "-"}</td>
                <td>${item.isTerminated ? "퇴사" : "재직 중"}</td>
                <td><span class="status-badge pending">${item.status ?? "-"}</span></td>
                <td>${formatDateTime(item.createdAt)}</td>
            </tr>
        `).join("");
    }

    async function fetchEmployees() {
        const response = await fetch("/admin/employees", {
            method: "GET",
            headers: buildAuthHeaders()
        });

        if (handleUnauthorized(response)) {
            return null;
        }

        let data = null;
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            data = await response.json();
        }

        if (!response.ok) {
            throw new Error(
                data?.message ||
                data?.error ||
                "직원 목록을 불러오지 못했습니다."
            );
        }

        return data;
    }

    async function fetchPendingChecks() {
        const response = await fetch("/admin/background-checks/all/pending", {
            method: "GET",
            headers: buildAuthHeaders()
        });

        if (handleUnauthorized(response)) {
            return null;
        }

        let data = null;
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            data = await response.json();
        }

        if (!response.ok) {
            throw new Error(
                data?.message ||
                data?.error ||
                "Pending background check 목록을 불러오지 못했습니다."
            );
        }

        return data;
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

            if (response.ok) {
                localStorage.removeItem("accessToken");
                localStorage.removeItem("refreshToken");
                window.location.href = "/html/login.html";
                return;
            }

            if (response.status === 401 || response.status === 403) {
                localStorage.removeItem("accessToken");
                localStorage.removeItem("refreshToken");
                window.location.href = "/html/login.html";
                return;
            }

            let data = null;
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("application/json")) {
                data = await response.json();
            }

            showError(data?.message || data?.error || "로그아웃에 실패했습니다.");
        } catch (error) {
            console.error("로그아웃 실패:", error);
            showError("로그아웃 중 오류가 발생했습니다.");
        }
    }

    async function loadDashboard() {
        clearMessages();

        employeeTableBody.innerHTML = `
            <tr>
                <td colspan="4" class="empty-cell">데이터를 불러오는 중입니다.</td>
            </tr>
        `;

        pendingTableBody.innerHTML = `
            <tr>
                <td colspan="7" class="empty-cell">데이터를 불러오는 중입니다.</td>
            </tr>
        `;

        employeeCount.textContent = "-";
        pendingCount.textContent = "-";

        try {
            const [employees, pendingChecks] = await Promise.all([
                fetchEmployees(),
                fetchPendingChecks()
            ]);

            if (!employees || !pendingChecks) {
                return;
            }

            renderEmployees(employees);
            renderPendingChecks(pendingChecks);
            showInfo("대시보드 데이터를 불러왔습니다.");
        } catch (error) {
            console.error("대시보드 로딩 실패:", error);
            showError(error.message || "대시보드 데이터를 불러오지 못했습니다.");
        }
    }

    if (!requireLogin()) {
        return;
    }

    refreshButton.addEventListener("click", loadDashboard);
    logoutButton.addEventListener("click", logout);

    loadDashboard();
});