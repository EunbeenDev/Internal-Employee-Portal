document.addEventListener("DOMContentLoaded", () => {

    const accountId = document.getElementById("accountId");
    const password = document.getElementById("password");
    const firstName = document.getElementById("firstName");
    const lastName = document.getElementById("lastName");
    const dateOfBirth = document.getElementById("dateOfBirth");
    const email = document.getElementById("email");
    const department = document.getElementById("department");

    const submitButton = document.getElementById("submitButton");
    const resetButton = document.getElementById("resetButton");
    const logoutButton = document.getElementById("logoutButton");

    const errorMessage = document.getElementById("errorMessage");
    const infoMessage = document.getElementById("infoMessage");


    function getAccessToken(){
        return localStorage.getItem("accessToken");
    }


    function showError(msg){
        errorMessage.textContent = msg;
        infoMessage.textContent = "";
    }

    function showInfo(msg){
        infoMessage.textContent = msg;
        errorMessage.textContent = "";
    }

    function clearMessages(){
        errorMessage.textContent = "";
        infoMessage.textContent = "";
    }


    function requireLogin(){
        const token = getAccessToken();

        if(!token){
            window.location.href = "/html/login.html";
            return false;
        }

        return true;
    }


    function buildAuthHeaders(){
        return {
            "Content-Type":"application/json",
            "Authorization":`Bearer ${getAccessToken()}`
        };
    }


    function handleUnauthorized(response){

        if(response.status === 401 || response.status === 403){

            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");

            window.location.href="/html/login.html";

            return true;
        }

        return false;

    }


    async function createAccount(){

        clearMessages();

        if(password.value.length < 8){
            showError("비밀번호는 최소 8자 이상이어야 합니다.");
            return;
        }

        if(!department.value.trim()){
            showError("부서명은 필수입니다.");
            return;
        }

        const body = {

            accountId: accountId.value,
            password: password.value,
            firstName: firstName.value,
            lastName: lastName.value,
            dateOfBirth: dateOfBirth.value,
            email: email.value,
            department: department.value.trim()

        };

        try{

            const response = await fetch("/admin/users/account",{

                method:"POST",
                headers:buildAuthHeaders(),
                body:JSON.stringify(body)

            });

            if(handleUnauthorized(response)) return;

            const data = await response.json();

            if(!response.ok){

                throw new Error(
                    data?.message ||
                    data?.error ||
                    "계정 생성에 실패했습니다."
                );

            }

            showInfo("계정 생성이 완료되었습니다.");

            document.getElementById("createAccountForm").reset();

        }catch(error){

            console.error(error);
            showError(error.message);

        }

    }


    function resetForm(){

        document.getElementById("createAccountForm").reset();
        clearMessages();

    }


    async function logout(){

        try{

            await fetch("/auth/logout",{

                method:"POST",
                headers:{
                    Authorization:`Bearer ${getAccessToken()}`
                }

            });

        }catch(e){}

        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");

        window.location.href="/html/login.html";

    }



    if(!requireLogin()) return;

    submitButton.addEventListener("click",createAccount);
    resetButton.addEventListener("click",resetForm);
    logoutButton.addEventListener("click",logout);

});