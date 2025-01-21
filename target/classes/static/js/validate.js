// Id 설정
function validateId(input) {
    if (input.value.length < 4) {
        input.setCustomValidity('아이디는 5자리 이상이어야 합니다.');
    } else {
        input.setCustomValidity('');
    }
}

// 비밀 번호 설정
function validatePassword(input) {
    if (input.value.length < 6) {
        input.setCustomValidity('비밀번호는 6자리 이상이어야 합니다.');
    } else {
        input.setCustomValidity('');
    }
}

// 이름 설정
function validateName(input) {
    if (input.value.length < 2) {
        input.setCustomValidity('이름은 두 글자 이상이어야 합니다.');
    }else{
        input.setCustomValidity('');
    }
    input.reportValidity();
}

// 전화번호 설정
function validateTel(input) {
    // 입력값에서 숫자만 추출
    const cleaned = input.value.replace(/[^0-9]/g, '');

    let formatted = '';

    if (cleaned.length <= 3) {
        // 3자리 이하인 경우 그대로 표시
        formatted = cleaned;
    } else if (cleaned.length <= 7) {
        // 4~7자리일 경우 중간에 하이픈 추가
        formatted = cleaned.slice(0, 3) + '-' + cleaned.slice(3);
    } else {
        // 8자리 이상일 경우 두 개의 하이픈 추가
        formatted = cleaned.slice(0, 3) + '-' + cleaned.slice(3, 7) + '-' + cleaned.slice(7, 11);
    }
    // 자동 포맷팅된 값을 입력 필드에 설정
    input.value = formatted;
    // 전화번호가 11자리인지 확인
    if (cleaned.length !== 11) {
        input.setCustomValidity('전화번호는 11자리로 입력해야 합니다.');
    } else {
        input.setCustomValidity('');
    }
}

// 제목 설정
function validateTitle(input) {
    if (input.value.length < 0) {
        input.setCustomValidity('제목을 입력하세요.');
    } else {
        input.setCustomValidity('');
    }
}

// 내용 설정
function validateContent(input) {
    if (input.value.length < 10) {
        input.setCustomValidity('최소 10글자 이상 입력하셔야합니다.');
    } else {
        input.setCustomValidity('');
    }
}

// 날짜 설정
function validateDate(input) {
    const datePattern = /^([0-9]{4})-([0-9]{2})-([0-9]{2})$/;
    if (!datePattern.test(input.value)) {
        input.setCustomValidity('0000-00-00 형식으로 입력하세요');
    } else {
        input.setCustomValidity('');
    }
}

function validatePasswordCheck(input) {
    const password = document.getElementById('password').value; // 비밀번호 필드 값
    const errorDiv = document.getElementById('passwordError'); // 에러 메시지 표시 영역

    if (input.value === '') {
        // 입력 필드가 비어 있으면 에러 메시지 제거
        input.setCustomValidity('');
        errorDiv.textContent = '';
    } else if (input.value !== password) {
        // 비밀번호가 일치하지 않으면 에러 메시지 표시
        input.setCustomValidity('비밀번호가 일치하지 않습니다.');
        errorDiv.textContent = '비밀번호가 일치하지 않습니다.';
    } else {
        // 비밀번호가 일치하면 에러 메시지 제거
        input.setCustomValidity('');
        errorDiv.textContent = '';
    }
}

