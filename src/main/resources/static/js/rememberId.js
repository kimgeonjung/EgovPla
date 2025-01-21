$(document).ready(function() {
    const rememberIdCheck = $("#flexCheckDefault");
    const loginIdInput = $("#loginId");

    const savedLoginId = localStorage.getItem("savedLoginId");
    console.log("savedLoginId:", savedLoginId);
    if (savedLoginId) {
        loginIdInput.val(savedLoginId);
        rememberIdCheck.prop("checked", true);
    }

    rememberIdCheck.on("click", function() {
        if (rememberIdCheck.prop("checked")) {
            localStorage.setItem("savedLoginId", loginIdInput.val());
            console.log("ID 저장:", loginIdInput.val()); // 저장된 ID 확인
        } else {
            localStorage.removeItem("savedLoginId");
            console.log("ID 삭제");
        }
    });
});