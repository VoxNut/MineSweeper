import { firebase } from "./firebase-init.js";
import { showAlert } from "./ui-alert.js";

console.log("auth.js loaded, firebase:", firebase);
const loginButton = document.getElementById("login-btn");
const modeShell = document.getElementById("mode-shell");
const modeSigninBtn = document.getElementById("mode-signin-btn");
const modeRegisterBtn = document.getElementById("mode-register-btn");
const switchToRegisterBtn = document.getElementById("switch-to-register");
const switchToSigninBtn = document.getElementById("switch-to-signin");
const emailLoginBtn = document.getElementById("email-login-btn");
const emailRegisterBtn = document.getElementById("email-register-btn");
const signinEmailInput = document.getElementById("signin-email");
const signinPasswordInput = document.getElementById("signin-password");
const registerNameInput = document.getElementById("register-name");
const registerEmailInput = document.getElementById("register-email");
const registerPasswordInput = document.getElementById("register-password");
const registerConfirmPasswordInput = document.getElementById("register-confirm-password");
const resetPasswordBtn = document.getElementById("reset-password-btn");
const resetEmailInput = document.getElementById("reset-email");
const signinEmailError = document.getElementById("signin-email-error");
const signinPasswordError = document.getElementById("signin-password-error");
const registerNameError = document.getElementById("register-name-error");
const registerEmailError = document.getElementById("register-email-error");
const registerPasswordError = document.getElementById("register-password-error");
const registerConfirmPasswordError = document.getElementById("register-confirm-password-error");

console.log("loginButton element:", loginButton);
const ctx = document.body.dataset.contextPath || "";

function setMode(mode) {
    if (modeShell) {
        modeShell.dataset.mode = mode;
    }
    if (modeSigninBtn && modeRegisterBtn) {
        const isSignin = mode === "signin";
        modeSigninBtn.className = isSignin ? "btn primary" : "btn ghost";
        modeRegisterBtn.className = isSignin ? "btn ghost" : "btn primary";
    }
    clearAuthErrors();
}

function clearFieldError(element) {
    if (element) {
        element.textContent = "";
    }
}

function setFieldError(element, message) {
    if (element) {
        element.textContent = message || "";
    }
}

function clearAuthErrors() {
    clearFieldError(signinEmailError);
    clearFieldError(signinPasswordError);
    clearFieldError(registerNameError);
    clearFieldError(registerEmailError);
    clearFieldError(registerPasswordError);
    clearFieldError(registerConfirmPasswordError);
}

function setLoading(button, isLoading, busyLabel) {
    if (!button) {
        return;
    }
    if (isLoading) {
        button.dataset.originalText = button.textContent;
        button.disabled = true;
        button.textContent = busyLabel;
    } else {
        button.disabled = false;
        if (button.dataset.originalText) {
            button.textContent = button.dataset.originalText;
            delete button.dataset.originalText;
        }
    }
}

function handleEnter(event, action) {
    if (event.key === "Enter") {
        event.preventDefault();
        action();
    }
}

async function login() {
    try {
        // [UC-01] 1.1.2 Hệ thống (Frontend) gọi Firebase SDK để mở cửa sổ popup xác thực Google.
        const provider = new firebase.auth.GoogleAuthProvider();
        const result = await firebase.auth().signInWithPopup(provider);
        // [UC-01] 1.1.3 Người dùng chọn một tài khoản Google hợp lệ và đồng ý cấp quyền. (Thực hiện trong popup)
        // [UC-01] 1.1.4 Firebase SDK nhận thông tin và trả về idToken cho Frontend.
        const idToken = await result.user.getIdToken();

        // [UC-01] 1.1.5 Frontend gửi request POST chứa idToken lên Backend (/auth/login).
        const response = await fetch(ctx + "/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ idToken })
        });
        const data = await response.json();
        if (!response.ok || !data.success) {
            throw new Error(data.message || "Login failed");
        }
        // [UC-01] 1.1.13 Frontend xử lý phản hồi và chuyển hướng người dùng vào trang trò chơi (/game).
        window.location.href = ctx + "/home";
    } catch (err) {
        // [UC-01] 1.3.1 Firebase SDK trả về lỗi auth/popup-closed-by-user (hoặc lỗi khác).
        // [UC-01] 1.3.2 / 1.4.3 / 1.5.3 Frontend hiển thị thông báo lỗi.
        showAlert(err.message || "Login failed", "error");
        // [UC-01] 1.3.3 / 1.4.4 / 1.5.4 Luồng kết thúc.
    }
}

async function syncSessionFromUser(user) {
    try {
        // [UC-02] 2.1.5 / [UC-03] 3.1.6 Frontend yêu cầu cấp idToken từ đối tượng User vừa nhận được.
        const idToken = await user.getIdToken();

        // [UC-02] 2.1.6 / [UC-03] 3.1.7 Frontend gửi API POST tới /auth/login kèm theo idToken trên body.
        const response = await fetch(ctx + "/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ idToken })
        });
        const data = await response.json();
        if (!response.ok || !data.success) {
            // [UC-02] 2.6.3 / 2.7.3 / 2.5.3 / [UC-03] 3.4.3 Hiển thị lỗi do Backend ném về.
            throw new Error(data.message || "Login failed");
        }

        // [UC-02] 2.1.14 / [UC-03] 3.1.13 Frontend kiểm tra role và thực hiện redirect người dùng.
        // [UC-02] 2.3.1.1 Chuyển hướng /admin/users nếu role="admin".
        // [UC-02] 2.3.2.1 Chuyển hướng /game (hiện tại là /home) nếu role="player".
        window.location.href = data.role === "admin" ? ctx + "/admin/users" : ctx + "/home";
        return data;
    } catch (err) {
        throw err;
    }
}

async function emailLogin() {
    try {
        clearAuthErrors();
        // [UC-02] 2.1.1 Người dùng nhập Email, Password và kích hoạt sự kiện đăng nhập.
        const email = signinEmailInput ? signinEmailInput.value.trim() : "";
        const password = signinPasswordInput ? signinPasswordInput.value : "";
        
        // [UC-02] 2.1.2 Frontend tiến hành validate input để đảm bảo dữ liệu không bị để trống.
        // [UC-02] 2.4.1 Frontend phát hiện Email hoặc Password bị bỏ trống.
        if (!email) {
            // [UC-02] 2.4.2 Hiển thị dòng thông báo lỗi ngay bên dưới ô nhập liệu.
            setFieldError(signinEmailError, "Email is required.");
            // [UC-02] 2.4.3 Dừng tiến trình đăng nhập.
            return;
        }
        if (!password) {
            setFieldError(signinPasswordError, "Password is required.");
            return;
        }

        setLoading(emailLoginBtn, true, "Please wait...");
        // [UC-02] 2.1.3 Frontend gọi hàm signInWithEmailAndPassword của Firebase SDK.
        // [UC-02] 2.1.4 Firebase Auth đối chiếu dữ liệu và xác thực thành công.
        const result = await firebase.auth().signInWithEmailAndPassword(email, password);
        await syncSessionFromUser(result.user);
    } catch (err) {
        // [UC-02] 2.5.1 Firebase Auth báo lỗi xác thực (Invalid credentials).
        // [UC-02] 2.5.2 Frontend dùng hàm showAlert hiển thị cảnh báo "Email login failed".
        showAlert(err.message || "Email login failed", "error");
        // [UC-02] 2.5.3 Dừng tiến trình đăng nhập.
    } finally {
        setLoading(emailLoginBtn, false);
    }
}

async function emailRegister() {
    try {
        clearAuthErrors();
        // [UC-03] 3.1.1 Người dùng nhập thông tin (Name, Email, Password, Confirm Password) và kích hoạt sự kiện Đăng ký.
        const name = registerNameInput ? registerNameInput.value.trim() : "";
        const email = registerEmailInput ? registerEmailInput.value.trim() : "";
        const password = registerPasswordInput ? registerPasswordInput.value : "";
        const confirmPassword = registerConfirmPasswordInput ? registerConfirmPasswordInput.value : "";
        
        // [UC-03] 3.1.2 Frontend tiến hành validate để đảm bảo các dữ liệu hợp lệ.
        // [UC-03] 3.2.1 Frontend phát hiện lỗi trường bị bỏ trống hoặc password không hợp lệ.
        if (!email) {
            // [UC-03] 3.2.2 Hiển thị cảnh báo lỗi cụ thể.
            setFieldError(registerEmailError, "Email is required.");
            // [UC-03] 3.2.3 Hệ thống huỷ tiến trình đăng ký.
            return;
        }
        if (!name) {
            setFieldError(registerNameError, "Display name is required.");
            return;
        }
        if (!password) {
            setFieldError(registerPasswordError, "Password is required.");
            return;
        }
        if (password.length < 8) {
            setFieldError(registerPasswordError, "Password must be at least 8 characters.");
            return;
        }
        if (!confirmPassword) {
            setFieldError(registerConfirmPasswordError, "Confirm your password.");
            return;
        }
        if (password !== confirmPassword) {
            setFieldError(registerConfirmPasswordError, "Passwords do not match.");
            return;
        }

        setLoading(emailRegisterBtn, true, "Please wait...");
        // [UC-03] 3.1.3 Frontend gọi hàm createUserWithEmailAndPassword của Firebase SDK.
        // [UC-03] 3.1.4 Firebase Auth xử lý tạo tài khoản thành công và trả về thông tin User.
        const result = await firebase.auth().createUserWithEmailAndPassword(email, password);
        if (name) {
            // [UC-03] 3.1.5 Frontend tiếp tục gọi Firebase SDK (updateProfile) để cập nhật tên hiển thị (Name).
            await result.user.updateProfile({ displayName: name });
        }
        // [UC-03] 3.1.6 và 3.1.7 được thực thi bên trong syncSessionFromUser.
        await syncSessionFromUser(firebase.auth().currentUser || result.user);
    } catch (err) {
        // [UC-03] 3.3.1 Firebase Auth từ chối tạo tài khoản (hoặc 3.4.1 Backend lỗi token).
        // [UC-03] 3.3.2 / 3.4.3 Frontend dùng hàm showAlert hiển thị thông báo lỗi.
        showAlert(err.message || "Registration failed", "error");
        // [UC-03] 3.3.3 / 3.4.4 Hệ thống huỷ tiến trình đăng ký / đăng nhập.
    } finally {
        setLoading(emailRegisterBtn, false);
    }
}

async function sendPasswordReset() {
    try {
        // [UC-04] 4.1.1 Người dùng nhập Email và kích hoạt sự kiện gửi yêu cầu đặt lại mật khẩu.
        const email = resetEmailInput ? resetEmailInput.value.trim() : "";
        
        // [UC-04] 4.1.2 Frontend tiến hành validate để đảm bảo trường Email không bị để trống.
        // [UC-04] 4.2.1 Frontend phát hiện ô nhập liệu Email chưa có dữ liệu.
        if (!email) {
            // [UC-04] 4.2.2 Frontend hiển thị cảnh báo "Please enter your email address".
            showAlert("Please enter your email address", "error");
            // [UC-04] 4.2.3 Luồng xử lý kết thúc.
            return;
        }

        // [UC-04] 4.1.3 Frontend gọi hàm sendPasswordResetEmail của Firebase SDK.
        // [UC-04] 4.1.4 Firebase SDK gửi yêu cầu mạng tới máy chủ Firebase Auth.
        // [UC-04] 4.1.5 Firebase Auth tìm kiếm email, gửi email đặt lại mật khẩu và trả về thành công.
        // [UC-04] 4.1.6 Frontend tiếp nhận kết quả thành công từ SDK.
        await firebase.auth().sendPasswordResetEmail(email);
        
        // [UC-04] 4.1.7 Frontend dùng hàm showAlert hiển thị thông báo màu xanh.
        showAlert("Password reset email sent. Check your inbox.", "success");
    } catch (err) {
        // [UC-04] 4.3.1 Firebase Auth phát hiện Email không tồn tại, sai định dạng hoặc máy chủ lỗi.
        // [UC-04] 4.3.2 Firebase Auth trả về thông báo lỗi cho Frontend.
        // [UC-04] 4.3.3 Frontend dùng hàm showAlert hiển thị cảnh báo màu đỏ.
        showAlert(err.message || "Failed to send reset email", "error");
        // [UC-04] 4.3.4 Luồng xử lý kết thúc.
    }
}

if (loginButton) {
    console.log("Attaching click handler to login button");
    // [UC-01] 1.1.1 Người dùng nhấn nút "Sign in with Google".
    loginButton.addEventListener("click", login);
} else {
    console.error("ERROR: login-btn element not found!");
}

if (modeSigninBtn) {
    modeSigninBtn.addEventListener("click", () => setMode("signin"));
}

if (modeRegisterBtn) {
    modeRegisterBtn.addEventListener("click", () => setMode("register"));
}

if (switchToRegisterBtn) {
    switchToRegisterBtn.addEventListener("click", () => setMode("register"));
}

if (switchToSigninBtn) {
    switchToSigninBtn.addEventListener("click", () => setMode("signin"));
}

if (emailLoginBtn) {
    emailLoginBtn.addEventListener("click", emailLogin);
}

if (signinPasswordInput) {
    signinPasswordInput.addEventListener("keydown", (event) => handleEnter(event, emailLogin));
}

if (emailRegisterBtn) {
    emailRegisterBtn.addEventListener("click", emailRegister);
}

if (registerPasswordInput) {
    registerPasswordInput.addEventListener("keydown", (event) => handleEnter(event, emailRegister));
}

if (registerConfirmPasswordInput) {
    registerConfirmPasswordInput.addEventListener("keydown", (event) => handleEnter(event, emailRegister));
}

if (resetPasswordBtn) {
    resetPasswordBtn.addEventListener("click", sendPasswordReset);
}

setMode("signin");
