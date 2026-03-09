document.addEventListener("DOMContentLoaded", () => {
    const myInfoEmptyMessage = document.getElementById("myInfoEmptyMessage");
    const myInfoViewSection = document.getElementById("myInfoViewSection");

    const employeeCodeValue = document.getElementById("employeeCodeValue");
    const nameValue = document.getElementById("nameValue");
    const emailValue = document.getElementById("emailValue");
    const dateOfBirthValue = document.getElementById("dateOfBirthValue");
    const departmentNameValue = document.getElementById("departmentNameValue");

    const refreshButton = document.getElementById("refreshButton");
    const openEditModalButton = document.getElementById("openEditModalButton");
    const logoutButton = document.getElementById("logoutButton");

    const editModalOverlay = document.getElementById("editModalOverlay");
    const closeEditModalButton = document.getElementById("closeEditModalButton");
    const cancelEditButton = document.getElementById("cancelEditButton");
    const saveEditButton = document.getElementById("saveEditButton");

    const editFirstName = document.getElementById("editFirstName");
    const editLastName = document.getElementById("editLastName");
    const editEmail = document.getElementById("editEmail");
    const editDateOfBirth = document.getElementById("editDateOfBirth");

    const errorMessage = document.getElementById("errorMessage");
    const infoMessage = document.getElementById("infoMessage");

    let currentMyInfo = null;

    function getAccessToken() {
        return localStorage.getItem("accessToken");
    }

    function getRole() {
        return localStorage.getItem("role");
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

    function requireLoginAndUserRole() {
        const token = getAccessToken();
        const role = getRole();

        if (!token) {
            window.location.href = "/html/login.html";
            return false;
        }

        if (role && role !== "ROLE_USER") {
            showError("직원 전용 페이지입니다.");
            return false;
        }

        return true;
    }

    function buildJsonHeaders() {
        return {
            "Authorization": `Bearer ${getAccessToken()}`,
            "Content-Type": "application/json"
        };
    }

    function handleUnauthorized(response) {
        if (response.status === 401 || response.status === 403) {
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");
            localStorage.removeItem("role");
            window.location.href = "/html/login.html";
            return true;
        }
        return false;
    }

    async function parseResponse(response) {
        const contentType = response.headers.get("content-type") || "";

        if (contentType.includes("application/json")) {
            return await response.json();
        }

        return await response.text();
    }

    function parseName(fullName) {
        if (!fullName || !fullName.trim()) {
            return ["", ""];
        }

        const trimmed = fullName.trim();

        if (trimmed.length === 2) {
            return [trimmed[1], trimmed[0]];
        }

        if (trimmed.length === 3) {
            return [trimmed.slice(1), trimmed[0]];
        }

        const parts = trimmed.split(" ");
        if (parts.length >= 2) {
            return [parts[0], parts.slice(1).join(" ")];
        }

        return [trimmed, ""];
    }

    function renderMyInfo(data) {
        if (!data || typeof data !== "object") {
            myInfoViewSection.style.display = "none";
            myInfoEmptyMessage.style.display = "block";
            myInfoEmptyMessage.textContent = "조회 가능한 내 정보가 없습니다.";
            return;
        }

        currentMyInfo = data;

        myInfoViewSection.style.display = "grid";
        myInfoEmptyMessage.style.display = "none";

        employeeCodeValue.textContent = data.employeeCode ?? "-";
        nameValue.textContent = data.name ?? "-";
        emailValue.textContent = data.email ?? "-";
        dateOfBirthValue.textContent = data.dateOfBirth ?? "-";
        departmentNameValue.textContent = data.departmentName ?? "-";
    }

    function fillEditModal(data) {
        const [firstName, lastName] = parseName(data?.name ?? "");

        editFirstName.value = firstName ?? "";
        editLastName.value = lastName ?? "";
        editEmail.value = data?.email ?? "";
        editDateOfBirth.value = data?.dateOfBirth ?? "";
    }

    function openEditModal() {
        if (!currentMyInfo) {
            showError("먼저 내 정보를 조회해주세요.");
            return;
        }

        fillEditModal(currentMyInfo);
        editModalOverlay.style.display = "flex";
    }

    function closeEditModal() {
        editModalOverlay.style.display = "none";
    }

    async function loadMyInfo() {
        clearMessages();

        myInfoViewSection.style.display = "none";
        myInfoEmptyMessage.style.display = "block";
        myInfoEmptyMessage.textContent = "내 정보를 불러오는 중입니다.";

        try {
            const response = await fetch("/api/v1/me", {
                method: "GET",
                headers: buildJsonHeaders()
            });

            if (handleUnauthorized(response)) {
                return;
            }

            const data = await parseResponse(response);

            if (!response.ok) {
                throw new Error(
                    typeof data === "object"
                        ? (data?.message || data?.error || "내 정보 조회에 실패했습니다.")
                        : (data || "내 정보 조회에 실패했습니다.")
                );
            }

            if (typeof data !== "object" || data === null) {
                throw new Error("내 정보 응답 형식이 올바르지 않습니다.");
            }

            renderMyInfo(data);
            showInfo("내 정보를 불러왔습니다.");
        } catch (error) {
            console.error("내 정보 조회 실패:", error);
            myInfoViewSection.style.display = "none";
            myInfoEmptyMessage.style.display = "block";
            myInfoEmptyMessage.textContent = "내 정보를 불러오지 못했습니다.";
            showError(error.message || "내 정보 조회 중 오류가 발생했습니다.");
        }
    }

    async function updateMyInfo() {
        clearMessages();

        const firstName = editFirstName.value.trim();
        const lastName = editLastName.value.trim();
        const email = editEmail.value.trim();
        const dateOfBirth = editDateOfBirth.value;

        if (!firstName) {
            showError("First Name을 입력해주세요.");
            editFirstName.focus();
            return;
        }

        if (!lastName) {
            showError("Last Name을 입력해주세요.");
            editLastName.focus();
            return;
        }

        if (!email) {
            showError("이메일을 입력해주세요.");
            editEmail.focus();
            return;
        }

        if (!dateOfBirth) {
            showError("생년월일을 입력해주세요.");
            editDateOfBirth.focus();
            return;
        }

        saveEditButton.disabled = true;
        saveEditButton.textContent = "저장 중...";

        try {
            const response = await fetch("/api/v1/me", {
                method: "PATCH",
                headers: buildJsonHeaders(),
                body: JSON.stringify({
                    firstName,
                    lastName,
                    email,
                    dateOfBirth
                })
            });

            if (handleUnauthorized(response)) {
                return;
            }

            const data = await parseResponse(response);

            if (!response.ok) {
                throw new Error(
                    typeof data === "object"
                        ? (data?.message || data?.error || "개인 정보 수정에 실패했습니다.")
                        : (data || "개인 정보 수정에 실패했습니다.")
                );
            }

            showInfo(
                typeof data === "object"
                    ? (data?.message || "개인 정보 수정이 완료되었습니다.")
                    : (data || "개인 정보 수정이 완료되었습니다.")
            );

            closeEditModal();
            await loadMyInfo();
        } catch (error) {
            console.error("개인 정보 수정 실패:", error);
            showError(error.message || "개인 정보 수정 중 오류가 발생했습니다.");
        } finally {
            saveEditButton.disabled = false;
            saveEditButton.textContent = "저장";
        }
    }

    async function logout() {
        try {
            await fetch("/auth/logout", {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${getAccessToken()}`
                }
            });
        } catch (error) {
            console.error("로그아웃 요청 실패:", error);
        }

        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        localStorage.removeItem("role");
        window.location.href = "/html/login.html";
    }

    if (!requireLoginAndUserRole()) {
        return;
    }

    refreshButton.addEventListener("click", loadMyInfo);
    openEditModalButton.addEventListener("click", openEditModal);
    closeEditModalButton.addEventListener("click", closeEditModal);
    cancelEditButton.addEventListener("click", closeEditModal);
    saveEditButton.addEventListener("click", updateMyInfo);
    logoutButton.addEventListener("click", logout);

    editModalOverlay.addEventListener("click", (event) => {
        if (event.target === editModalOverlay) {
            closeEditModal();
        }
    });

    loadMyInfo();
});