import { firebase } from "./firebase-init.js";
import { showAlert } from "./ui-alert.js";

const adminLoginBtn = document.getElementById("admin-login-btn");
const adminPasswordInput = document.getElementById("admin-password");
const ctx = document.body.dataset.contextPath || "";

async function adminLogin() {
    try {
        const password = adminPasswordInput ? adminPasswordInput.value : "";
        if (!password) {
            showAlert("Please enter the admin password", "error");
            return;
        }

        const result = await firebase.auth().signInWithEmailAndPassword("msadmin@minesweeper.local", password);
        const idToken = await result.user.getIdToken();

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
    } catch (err) {
        showAlert(err.message || "Admin login failed", "error");
    }
}

if (adminLoginBtn) {
    adminLoginBtn.addEventListener("click", adminLogin);
}