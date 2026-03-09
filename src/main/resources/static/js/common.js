// 공통 유틸리티 함수

// 세션 체크
function checkSession() {
    // 로그인 페이지가 아닌 경우에만 체크
    if (window.location.pathname === '/admin-view/login') {
        return;
    }

    // 간단한 세션 확인 (실제로는 서버에서 확인)
    fetch('/admin-view/api/employees')
        .then(response => {
            if (response.status === 401) {
                window.location.href = '/admin-view/login';
            }
        })
        .catch(() => {
            // 네트워크 오류는 무시
        });
}

// API 호출 헬퍼
async function apiCall(url, options = {}) {
    try {
        const response = await fetch(url, {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            }
        });

        if (response.status === 401) {
            window.location.href = '/admin-view/login';
            return null;
        }

        const data = await response.json();
        return { success: response.ok, data, status: response.status };
    } catch (error) {
        console.error('API 호출 오류:', error);
        return { success: false, error: error.message };
    }
}

// 알림 메시지 표시
function showMessage(message, type = 'info') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type}`;
    alertDiv.textContent = message;

    const container = document.querySelector('.container') || document.body;
    container.insertBefore(alertDiv, container.firstChild);

    setTimeout(() => {
        alertDiv.style.opacity = '0';
        alertDiv.style.transition = 'opacity 0.3s';
        setTimeout(() => alertDiv.remove(), 300);
    }, 3000);
}

// 에러 메시지 표시
function showError(message) {
    showMessage(message, 'error');
}

// 성공 메시지 표시
function showSuccess(message) {
    showMessage(message, 'success');
}

// 날짜 포맷팅
function formatDate(dateString) {
    if (!dateString) return '-';
    try {
        const date = new Date(dateString);
        return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    } catch {
        return dateString;
    }
}

// 상태 뱃지 생성
function getStatusBadge(status) {
    const statusLower = status.toLowerCase();
    let badgeClass = 'badge';

    if (statusLower === 'pending') {
        badgeClass += ' badge-pending';
    } else if (statusLower === 'clear') {
        badgeClass += ' badge-clear';
    } else if (statusLower === 'flagged') {
        badgeClass += ' badge-flagged';
    }

    return `<span class="${badgeClass}">${status}</span>`;
}

// 로그아웃
async function logout() {
    try {
        const result = await apiCall('/admin-view/api/logout', { method: 'POST' });
        if (result && result.success) {
            window.location.href = '/admin-view/login';
        }
    } catch (error) {
        console.error('로그아웃 오류:', error);
        window.location.href = '/admin-view/login';
    }
}

// URL 파라미터 가져오기
function getUrlParameter(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
}

// 페이지 로드 시 세션 체크
document.addEventListener('DOMContentLoaded', () => {
    checkSession();
});

