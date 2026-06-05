const ROOT_ID = "alert-root";

function ensureAlertRoot() {
    let root = document.getElementById(ROOT_ID);
    if (!root) {
        root = document.createElement("div");
        root.id = ROOT_ID;
        root.className = "alert-root";
        document.body.appendChild(root);
    }
    return root;
}

export function clearAlerts() {
    const root = document.getElementById(ROOT_ID);
    if (root) {
        root.innerHTML = "";
    }
}

export function showAlert(message, type = "info", options = {}) {
    const root = ensureAlertRoot();
    const alertEl = document.createElement("div");
    const safeType = type || "info";
    const text = document.createElement("span");
    const close = document.createElement("button");

    alertEl.className = `alert ${safeType}`;
    alertEl.setAttribute("role", "alert");

    text.className = "alert-text";
    text.textContent = String(message || "Something went wrong.");

    close.className = "alert-close";
    close.type = "button";
    close.textContent = "Close";
    close.addEventListener("click", () => {
        alertEl.classList.remove("show");
        setTimeout(() => alertEl.remove(), 200);
    });

    alertEl.appendChild(text);
    alertEl.appendChild(close);
    root.appendChild(alertEl);

    requestAnimationFrame(() => {
        alertEl.classList.add("show");
    });

    const duration = typeof options.duration === "number" ? options.duration : 4200;
    if (duration > 0) {
        setTimeout(() => {
            alertEl.classList.remove("show");
            setTimeout(() => alertEl.remove(), 200);
        }, duration);
    }

    return alertEl;
}
