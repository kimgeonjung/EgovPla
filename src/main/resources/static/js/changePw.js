$(document).ready(function() {
    const loginId = $('#loginId');
    const hiddenLoginId = $('#hidden-login-id');
    hiddenLoginId.val(loginId.val());

    $('#ch-pw').on('submit', function (event) {
        event.preventDefault();
        const data = {
            loginId: $('#loginId').val(),
            oldPw: $('#oldPw').val(),
            newPw: $('#newPw').val(),
            confirmPw: $('#confirmPw').val(),
        };

        $.ajax({
            url: '/api/member/changePw',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (response) {
                alert('비밀번호 변경 성공. 다시 로그인 해주세요.');
                window.location.href = "/login";
            },
            error: function (error) {
                alert('비밀번호 변경 실패');
            }
        });
    });
});