document.addEventListener("DOMContentLoaded", () => {
    const pendingCount = document.getElementById("pendingCount");
    const pendingTableBody = document.getElementById("pendingTableBody");
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
        const token = getAccessToken();
        if (!token) {
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

    function formatDateTime(value) {
        if (!value) return "-";
        const date = new Date(value);
        if (Number.isNaN(date.getTime())) return value;
        return date.toLocaleString("ko-KR");
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

    function renderPendingChecks(items) {
        if (!Array.isArray(items) || items.length === 0) {
            pendingCount.textContent = "0";
            pendingTableBody.innerHTML = `
                <tr>
                    <td colspan="7" class="empty-cell">현재 pending 상태의 background check가 없습니다.</td>
                </tr>
            `;
            return;
        }

        pendingCount.textContent = String(items.length);

        pendingTableBody.innerHTML = items.map(item => {
            const statusRaw = (item.status ?? "-").toString().toLowerCase();
            const terminatedStatus = item.isTerminated ? "퇴사" : "재직 중";

            return `
                <tr>
                    <td>${escapeHtml(item.checkId ?? "-")}</td>
                    <td>${escapeHtml(item.employeeId ?? "-")}</td>
                    <td>${escapeHtml(item.employeeName ?? "-")}</td>
                    <td>${escapeHtml(item.dateOfBirth ?? "-")}</td>
                    <td>${terminatedStatus}</td>
                    <td><span class="status-badge pending">${escapeHtml(statusRaw)}</span></td>
                    <td>${escapeHtml(formatDateTime(item.createdAt))}</td>
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

    async function fetchPendingChecks() {
        const response = await fetch("/admin/background-checks/all/pending", {
            method: "GET",
            headers: buildAuthHeaders()
        });

        if (handleUnauthorized(response)) {
            return null;
        }

        const responseData = await parseJsonIfExists(response);

        if (!response.ok) {
            throw new Error(
                responseData?.message ||
                responseData?.error ||
                "Pending background check 목록을 불러오지 못했습니다."
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

            const responseData = await parseJsonIfExists(response);

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

    async function loadPendingChecks() {
        clearMessages();

        pendingCount.textContent = "-";
        pendingTableBody.innerHTML = `
            <tr>
                <td colspan="7" class="empty-cell">데이터를 불러오는 중입니다.</td>
            </tr>
        `;

        try {
            const pendingChecks = await fetchPendingChecks();

            if (!pendingChecks) {
                return;
            }

            renderPendingChecks(pendingChecks);
            showInfo("Pending background check 목록을 불러왔습니다.");
        } catch (error) {
            console.error("Pending 목록 조회 실패:", error);
            pendingCount.textContent = "-";
            pendingTableBody.innerHTML = `
                <tr>
                    <td colspan="7" class="empty-cell">Pending 목록을 불러오지 못했습니다.</td>
                </tr>
            `;
            showError(error.message || "Pending 목록 조회 중 오류가 발생했습니다.");
        }
    }

    if (!requireLogin()) {
        return;
    }

    refreshButton.addEventListener("click", loadPendingChecks);
    logoutButton.addEventListener("click", logout);

    loadPendingChecks();
});