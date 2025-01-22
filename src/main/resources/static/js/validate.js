// 아이디 검증 함수
function validateId(input) {
    // 아이디 길이 확인 (5자 이상, 12자 이하)
    const idLength = input.value.length;
    const regex = /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{5,12}$/; // 영어와 숫자가 포함된 5~12자리 아이디

    if (idLength < 5 || idLength > 12) {
        input.setCustomValidity('아이디는 5자리 이상 12자리 이하이어야 합니다.');
    } else if (!regex.test(input.value)) {
        input.setCustomValidity('아이디는 영어와 숫자가 모두 포함되어야 합니다.');
    } else {
        input.setCustomValidity(''); // 조건 만족시 유효
    }
}


// 비밀 번호 설정
function validatePassword(input) {
    // 비밀번호가 6자리 이상이고, 특수문자를 포함하는지 확인
    const regex = /[!@#$%^&*(),.?":{}|<>_\-+=]/;  // 특수문자 정규식

    if (input.value.length < 6) {
        input.setCustomValidity('비밀번호는 6자리 이상이어야 합니다.');
    } else if (!regex.test(input.value)) {
        input.setCustomValidity('비밀번호는 특수 문자를 포함해야 합니다. 예: !, @, #, $, %, ^, &, *');
    } else {
        input.setCustomValidity('');  // 모든 조건을 만족하면 오류 메시지 초기화
    }
}
function validateNewPassword(input) {
    // 비밀번호가 6자리 이상이고, 특수문자를 포함하는지 확인
    const regex = /[!@#$%^&*(),.?":{}|<>_\-+=]/;  // 특수문자 정규식
    // 현재 비밀번호 가져오기
    const oldPw = document.getElementById('oldPw').value;
    if (input.value.length < 6) {
        input.setCustomValidity('비밀번호는 6자리 이상이어야 합니다.');
    } else if (!regex.test(input.value)) {
        input.setCustomValidity('비밀번호는 특수 문자를 포함해야 합니다. 예: !, @, #, $, %, ^, &, *');
    } else if (input.value === oldPw) {
        input.setCustomValidity('새 비밀번호는 현재 비밀번호와 다르게 설정해야 합니다.');
    } else {
        input.setCustomValidity('');  // 모든 조건을 만족하면 오류 메시지 초기화
    }
}

// 이름 검증 함수
function validateName(input) {
    const nameLength = input.value.length;
    // 이름 길이 검증 (2자 이상 10자 이하)
    if (nameLength < 2) {
        input.setCustomValidity('이름은 두 글자 이상이어야 합니다.');
    } else if (nameLength > 10) {
        input.setCustomValidity('이름은 10자 이하이어야 합니다.');
    } else {
        input.setCustomValidity('');
    }

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

    // 전화번호가 11자리이고, 첫 3자리가 '010'인지 확인
    if (cleaned.length !== 11 || cleaned.slice(0, 3) !== '010') {
        input.setCustomValidity('전화번호는 010으로 시작하고 11자리로 입력해야 합니다.');
    } else {
        input.setCustomValidity('');
    }

}


function  validateZipcode(input){
// 우편번호 유효성 검사 (5자리 숫자)
    function validateZipcode(input) {
        if (!/^[0-9]{5}$/.test(input.value.trim())) {
            input.setCustomValidity('우편번호는 5자리 숫자로 입력해야 합니다.');
        } else {
            input.setCustomValidity('');
        }
        input.reportValidity(); // 브라우저 기본 유효성 검사 메시지 표시
    }
}
// 주소 유효성 검사 (공백 확인)
function validateAddress(input) {
    if (input.value.trim().length < 5) {
        input.setCustomValidity('주소는 최소 5자 이상 입력해야 합니다.');
    } else {
        input.setCustomValidity('');
    }
    input.reportValidity();
}

// 상세주소 유효성 검사 (공백 확인)
function validateDetailAddress(input) {
    if (input.value.trim() === '') {
        input.setCustomValidity('상세주소를 입력해주세요.');
    } else {
        input.setCustomValidity('');
    }
    input.reportValidity();
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
function validateNewPasswordCheck(input) {
    const newPw = document.getElementById('newPw').value;  // 새 비밀번호 입력값 가져오기
    const confirmPw = input.value;  // 비밀번호 확인 입력값 가져오기

    if (confirmPw === '') {
        input.setCustomValidity('비밀번호를 입력해주세요.');
    } else if (newPw !== confirmPw) {
        input.setCustomValidity('비밀번호가 일치하지 않습니다.');
    } else {
        input.setCustomValidity('');
    }
}

function validatePasswordCheck(input) {
    const password = document.getElementById('password').value; // 비밀번호 필드 값

    if (input.value === '') {
        // 입력 필드가 비어 있으면 에러 메시지 제거
        input.setCustomValidity('');

    } else if (input.value !== password) {
        // 비밀번호가 일치하지 않으면 에러 메시지 표시
        input.setCustomValidity('비밀번호가 일치하지 않습니다.');

    } else {
        // 비밀번호가 일치하면 에러 메시지 제거
        input.setCustomValidity('');

    }
}
