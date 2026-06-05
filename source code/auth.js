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
        const provider = new firebase.auth.GoogleAuthProvider();
        const result = await firebase.auth().signInWithPopup(provider);
        const idToken = await result.user.getIdToken();

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
        window.location.href = ctx + "/home";
    } catch (err) {
        showAlert(err.message || "Login failed", "error");
    }
}

async function syncSessionFromUser(user) {
    try {
        const idToken = await user.getIdToken();

        const response = await fetch(ctx + "/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ idToken })
        });
        const data = await response.json();
        if (!response.ok || !data.success) {
            throw new Error(data.message || "Admin login failed");
        }

        window.location.href = data.role === "admin" ? ctx + "/admin/users" : ctx + "/home";
        return data;
    } catch (err) {
        throw err;
    }
}

async function emailLogin() {
    try {
        clearAuthErrors();
        const email = signinEmailInput ? signinEmailInput.value.trim() : "";
        const password = signinPasswordInput ? signinPasswordInput.value : "";
        if (!email) {
            setFieldError(signinEmailError, "Email is required.");
            return;
        }
        if (!password) {
            setFieldError(signinPasswordError, "Password is required.");
            return;
        }

        setLoading(emailLoginBtn, true, "Please wait...");
        const result = await firebase.auth().signInWithEmailAndPassword(email, password);
        await syncSessionFromUser(result.user);
    } catch (err) {
        showAlert(err.message || "Email login failed", "error");
    } finally {
        setLoading(emailLoginBtn, false);
    }
}

async function emailRegister() {
    try {
        clearAuthErrors();
        const name = registerNameInput ? registerNameInput.value.trim() : "";
        const email = registerEmailInput ? registerEmailInput.value.trim() : "";
        const password = registerPasswordInput ? registerPasswordInput.value : "";
        const confirmPassword = registerConfirmPasswordInput ? registerConfirmPasswordInput.value : "";
        if (!email) {
            setFieldError(registerEmailError, "Email is required.");
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
        const result = await firebase.auth().createUserWithEmailAndPassword(email, password);
        if (name) {
            await result.user.updateProfile({ displayName: name });
        }
        await syncSessionFromUser(firebase.auth().currentUser || result.user);
    } catch (err) {
        showAlert(err.message || "Registration failed", "error");
    } finally {
        setLoading(emailRegisterBtn, false);
    }
}

async function sendPasswordReset() {
    try {
        const email = resetEmailInput ? resetEmailInput.value.trim() : "";
        if (!email) {
            showAlert("Please enter your email address", "error");
            return;
        }

        await firebase.auth().sendPasswordResetEmail(email);
        showAlert("Password reset email sent. Check your inbox.", "success");
    } catch (err) {
        showAlert(err.message || "Failed to send reset email", "error");
    }
}

if (loginButton) {
    console.log("Attaching click handler to login button");
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
