$(document).ready(function() {
    const rememberIdCheck = $("#flexCheckDefault");
    const loginIdInput = $("#loginId");

    const savedLoginId = localStorage.getItem("savedLoginId");
    if (savedLoginId) {
        loginIdInput.val(savedLoginId);
        rememberIdCheck.prop("checked", true);
    }

    rememberIdCheck.on("click", function() {
        if (rememberIdCheck.prop("checked")) {
            localStorage.setItem("savedLoginId", loginIdInput.val());
        } else {
            localStorage.removeItem("savedLoginId");
        }
    });
});