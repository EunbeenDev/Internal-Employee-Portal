document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("loginForm");
    const accountIdInput = document.getElementById("accountId");
    const passwordInput = document.getElementById("password");
    const loginButton = document.getElementById("loginButton");
    const errorMessage = document.getElementById("errorMessage");
    const infoMessage = document.getElementById("infoMessage");

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

    function setLoading(isLoading) {
        loginButton.disabled = isLoading;
        loginButton.textContent = isLoading ? "로그인 중..." : "로그인";
    }

    async function parseResponse(response) {
        const contentType = response.headers.get("content-type") || "";

        if (contentType.includes("application/json")) {
            return await response.json();
        }

        return await response.text();
    }

    function saveLoginInfo(accessToken, refreshToken, role) {
        localStorage.setItem("accessToken", accessToken);
        localStorage.setItem("refreshToken", refreshToken);
        localStorage.setItem("role", role);
    }

    function moveByRole(role) {
        if (role === "ROLE_ADMIN") {
            window.location.href = "/html/admin.html";
            return;
        }

        if (role === "ROLE_USER") {
            window.location.href = "/html/employee-my-info.html";
            return;
        }

        showError("로그인 응답에 유효한 권한 정보가 없습니다.");
    }

    loginForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        clearMessages();

        const accountId = accountIdInput.value.trim();
        const password = passwordInput.value;

        if (!accountId) {
            showError("계정 ID를 입력해주세요.");
            accountIdInput.focus();
            return;
        }

        if (!password) {
            showError("비밀번호를 입력해주세요.");
            passwordInput.focus();
            return;
        }

        const requestBody = {
            accountId,
            password
        };

        setLoading(true);

        try {
            const response = await fetch("/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(requestBody)
            });

            const responseData = await parseResponse(response);

            if (!response.ok) {
                const serverMessage =
                    typeof responseData === "object"
                        ? (responseData?.message || responseData?.error || responseData?.detail)
                        : responseData;

                showError(serverMessage || "로그인에 실패했습니다. 계정 정보를 다시 확인해주세요.");
                return;
            }

            if (typeof responseData !== "object" || responseData === null) {
                showError("로그인 응답 형식이 올바르지 않습니다.");
                return;
            }

            const accessToken = responseData.accessToken;
            const refreshToken = responseData.refreshToken;
            const role = responseData.role;

            if (!accessToken || !refreshToken) {
                showError("로그인 응답이 올바르지 않습니다. 토큰 정보가 없습니다.");
                return;
            }

            if (!role) {
                showError("로그인 응답에 권한 정보가 없습니다.");
                return;
            }

            saveLoginInfo(accessToken, refreshToken, role);
            showInfo("로그인에 성공했습니다.");

            moveByRole(role);
        } catch (error) {
            console.error("로그인 요청 실패:", error);
            showError("서버와 통신 중 오류가 발생했습니다.");
        } finally {
            setLoading(false);
        }
    });
});