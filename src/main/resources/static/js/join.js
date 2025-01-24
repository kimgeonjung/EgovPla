$(document).ready(function (){
    const emailInput = $('#email');
    const codeInput = $('#code');
    const hiddenEmailField = $('#hiddenEmail');
    const emailBtn = $('#email-btn');
    const codeBtn = $('#code-btn');
    const uniqueBtn = $('#unique_btn');

    const registerDataForm = $('#register-data-form');

    uniqueBtn.on('click', function (event) {
        event.preventDefault();
        uniqueBtn.prop("disabled", true)
            .text("중복 확인 중...");

        $.ajax({
            url: '/api/mail/uniqueCheck',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ email: emailInput.val() }),
            success: function (response) {
                if (response) {
                    alert('이미 존재하는 이메일입니다. 다시 시도해주세요.');
                } else {
                    alert('이메일 사용가능');
                    uniqueBtn.css('display', 'none'); // uniqueBtn 숨기기
                    emailBtn.css('display', 'inline-block'); // emailBtn 표시하기
                }
                uniqueBtn.prop("disabled", false).text("이메일 중복 확인");
            },
            error: function (xhr) {
                alert('오류가 발생했습니다. 다시 시도해 주세요.');
                console.error(xhr.responseText);
                uniqueBtn.prop("disabled", false).text("이메일 중복 확인");
            }
        });
    });


    // 이메일 인증번호 발송
    $('#checkEmail').on('submit', function (event) {
        event.preventDefault();
        emailBtn.prop("disabled", true)
                .text("인증 진행 중...");

        $.ajax({
            url: '/api/mail/emailCheck',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ email: emailInput.val() }),
            success: function (response) {
                alert('이메일 인증 번호 전송 완료');
                emailInput.prop('readonly', true); // 이메일 입력란 비활성화
                $('#checkCode').removeClass('d-none'); // 인증번호 확인 폼 표시
            },
            error: function (xhr) {
                alert('이메일 인증 번호 전송 실패');
                console.error(xhr.responseText);
                emailInput.prop('readonly', false);
                emailBtn.prop("disabled", false).text("이메일 인증");
            }
        });
    });

    // 인증번호 확인
    $('#checkCode').on('submit', function (event) {
        event.preventDefault();
        const email = emailInput.val();

        $.ajax({
            url: '/api/mail/codeCheck',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ email: email, code: codeInput.val() }),
            success: function (response) {
                alert('인증 성공');
                codeInput.prop('readonly', true);
                hiddenEmailField.val(email);
                emailBtn.removeClass('btn-outline-primary').addClass('btn-outline-success');
                emailBtn.text("인증 완료");
                codeBtn.removeClass('btn-outline-primary').addClass('btn-outline-success');
                codeBtn.prop("disabled", true).text("인증 완료");
                $("#checkId").removeClass('d-none');
            },
            error: function (error) {
                alert('인증 실패. 잘못된 인증번호입니다.');
                codeInput.val('');
                emailBtn.prop("disabled", false).text("이메일 인증");
                emailInput.prop('readonly', false).focus();
            }
        });
    });

    // ID 중복확인
    const hiddenLoginIdField = $('#hiddenLoginId');
    const loginIdInput = $('#loginId');
    const loginIdBtn = $('#loginId-btn');

    $("#checkId").on("submit", function (event) {
        event.preventDefault(); // 기본 폼 제출 동작 방지

        const id = loginIdInput.val();

        if (!id || id.trim().length === 0) {
            loginIdBtn.removeClass('btn-outline-primary')
                .addClass('btn-outline-danger').text("입력되지 않음");
            return false;
        }

        if (id.length < 5) {
            loginIdBtn.removeClass('btn-outline-primary')
                .addClass('btn-outline-danger').text("ID는 최소 5자 이상");
            return false;
        }

        // Ajax로 전송
        $.ajax({
            url: '/api/member/confirmId',
            type: 'POST',
            contentType: 'application/json',
            data: id, // 문자열로 전송
            success: function (result) {
                if (result) {
                    loginIdBtn.removeClass('btn-outline-primary btn-outline-danger')
                        .addClass('btn-outline-success')
                        .prop("disabled", true)
                        .text("사용 가능한 ID");
                    hiddenLoginIdField.val(id); // 숨겨진 필드에 ID 저장
                    loginIdInput.prop('readonly', true); // 입력 필드 비활성화
                    registerDataForm.removeClass('d-none')
                } else {
                    loginIdBtn.removeClass('btn-outline-primary')
                        .addClass('btn-outline-danger').text("사용중인 ID");
                    loginIdInput.val(''); // 입력 필드 초기화
                }
            },
            error: function () {
                loginIdBtn.removeClass('btn-outline-primary')
                    .addClass('btn-outline-danger').text("ID 확인 중 오류 발생");
            }
        });
    });

});