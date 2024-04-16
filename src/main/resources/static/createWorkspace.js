
function togglePasswordInput() {
    let passwordField = document.getElementById("passwordField");
    let needPassword = document.getElementById("needPassword");
    if (needPassword.checked) {
        passwordField.style.display = "block";
    } else {
        passwordField.style.display = "none";
    }
}