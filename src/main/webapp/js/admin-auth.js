import { firebase } from "./firebase-init.js";
import { showAlert } from "./ui-alert.js";

const adminLoginBtn = document.getElementById("admin-login-btn");
const adminPasswordInput = document.getElementById("admin-password");
const ctx = document.body.dataset.contextPath || "";

async function adminLogin() {
    try {
        // [UC-06] 6.1.1 Quản trị viên nhập mật khẩu bảo mật và kích hoạt sự kiện đăng nhập.
        const password = adminPasswordInput ? adminPasswordInput.value : "";
        
        // [UC-06] 6.1.2 Frontend tiến hành validate để đảm bảo trường Password không bị bỏ trống.
        // [UC-06] 6.3.1 Frontend phát hiện ô nhập mật khẩu trống.
        if (!password) {
            // [UC-06] 6.3.2 Dùng hàm showAlert hiển thị lỗi.
            showAlert("Please enter the admin password", "error");
            // [UC-06] 6.3.3 Huỷ luồng xử lý.
            return;
        }

        // [UC-06] 6.1.3 Frontend gọi Firebase SDK bằng tài khoản mặc định và mật khẩu.
        // [UC-06] 6.1.4 Firebase Auth xử lý xác thực mật khẩu.
        const result = await firebase.auth().signInWithEmailAndPassword("msadmin@minesweeper.local", password);
        // [UC-06] 6.1.5 Frontend trích xuất mã thông báo idToken (JWT) từ Firebase SDK.
        const idToken = await result.user.getIdToken();

        // [UC-06] 6.1.6 Frontend gửi yêu cầu HTTP POST kèm idToken tới Backend API (/auth/login).
        const response = await fetch(ctx + "/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ idToken })
        });
        const data = await response.json();
        // [UC-06] 6.2.1 Backend trả về thuộc tính role thông qua API phản hồi.
        if (!response.ok || !data.success) {
            throw new Error(data.message || "Admin login failed");
        }

        // [UC-06] 6.1.13 / 6.2.2 / 6.2.3 Frontend phân tích thuộc tính role và thực hiện chuyển hướng.
        window.location.href = data.role === "admin" ? ctx + "/admin/users" : ctx + "/home";
    } catch (err) {
        // [UC-06] 6.4.1 Firebase Auth đối chiếu thất bại (hoặc lỗi khác).
        // [UC-06] 6.4.2 Frontend bắt lỗi và hiển thị dòng chữ "Admin login failed".
        showAlert(err.message || "Admin login failed", "error");
        // [UC-06] 6.4.3 Huỷ luồng xử lý.
    }
}

if (adminLoginBtn) {
    adminLoginBtn.addEventListener("click", adminLogin);
}