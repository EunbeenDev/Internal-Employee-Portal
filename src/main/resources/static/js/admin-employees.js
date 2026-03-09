document.addEventListener("DOMContentLoaded", () => {
    const employeeCount = document.getElementById("employeeCount");
    const employeeTableBody = document.getElementById("employeeTableBody");
    const refreshButton = document.getElementById("refreshButton");
    const logoutButton = document.getElementById("logoutButton");
    const errorMessage = document.getElementById("errorMessage");
    const infoMessage = document.getElementById("infoMessage");

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
        const accessToken = getAccessToken();
        if (!accessToken) {
            window.location.href = "/html/login.html";
            return false;
        }
        return true;
    }

    function buildAuthHeaders() {
        return {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${getAccessToken()}`
        };
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

    function renderEmployees(employees) {
        if (!Array.isArray(employees) || employees.length === 0) {
            employeeCount.textContent = "0";
            employeeTableBody.innerHTML = `
                <tr>
                    <td colspan="5" class="empty-cell">조회된 직원이 없습니다.</td>
                </tr>
            `;
            return;
        }

        employeeCount.textContent = String(employees.length);

        employeeTableBody.innerHTML = employees.map(employee => {
            const employeeCode = employee.employeeCode ?? "-";
            const name = employee.name ?? "-";
            const department = employee.department ?? "-";
            const terminatedStatus = employee.isTerminated ? "퇴사" : "재직 중";

            return `
                <tr>
                    <td>${employeeCode}</td>
                    <td>${name}</td>
                    <td>${department}</td>
                    <td>${terminatedStatus}</td>
                    <td>
                        <a class="text-link" href="/html/admin-employee-detail.html?employeeCode=${encodeURIComponent(employeeCode)}">
                            상세 보기
                        </a>
                    </td>
                </tr>
            `;
        }).join("");
    }

    async function fetchEmployeeList() {
        const response = await fetch("/admin/employees", {
            method: "GET",
            headers: buildAuthHeaders()
        });

        if (handleUnauthorized(response)) {
            return null;
        }

        let responseData = null;
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            responseData = await response.json();
        }

        if (!response.ok) {
            throw new Error(
                responseData?.message ||
                responseData?.error ||
                "직원 목록을 불러오지 못했습니다."
            );
        }

        return responseData;
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

            let responseData = null;
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("application/json")) {
                responseData = await response.json();
            }

            showError(
                responseData?.message ||
                responseData?.error ||
                "로그아웃에 실패했습니다."
            );
        } catch (error) {
            console.error("로그아웃 실패:", error);
            showError("로그아웃 중 오류가 발생했습니다.");
        }
    }

    async function loadEmployees() {
        clearMessages();

        employeeCount.textContent = "-";
        employeeTableBody.innerHTML = `
            <tr>
                <td colspan="4" class="empty-cell">데이터를 불러오는 중입니다.</td>
            </tr>
        `;

        try {
            const employees = await fetchEmployeeList();

            if (!employees) {
                return;
            }

            renderEmployees(employees);
            showInfo("직원 목록을 불러왔습니다.");
        } catch (error) {
            console.error("직원 목록 조회 실패:", error);
            employeeCount.textContent = "-";
            employeeTableBody.innerHTML = `
                <tr>
                    <td colspan="4" class="empty-cell">직원 목록을 불러오지 못했습니다.</td>
                </tr>
            `;
            showError(error.message || "직원 목록 조회 중 오류가 발생했습니다.");
        }
    }

    if (!requireLogin()) {
        return;
    }

    refreshButton.addEventListener("click", loadEmployees);
    logoutButton.addEventListener("click", logout);

    loadEmployees();
});