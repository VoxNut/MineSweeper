import { showAlert } from "./ui-alert.js";
import { playSound } from "./sound.js";

const ctx = document.body.dataset.contextPath || "";
const boardEl = document.getElementById("board");
const timerEl = document.getElementById("timer");
const mineCountEl = document.getElementById("mine-count");
const difficultySelect = document.getElementById("difficulty");
const customFields = document.getElementById("custom-fields");
const customRows = document.getElementById("custom-rows");
const customCols = document.getElementById("custom-cols");
const customMines = document.getElementById("custom-mines");
const newGameBtn = document.getElementById("new-game");
const overlayEl = document.getElementById("overlay");
const overlayTitle = document.getElementById("overlay-title");
const overlayNew = document.getElementById("overlay-new");

let boardState = null;
let started = false;
let gameOver = false;
let flaggedCount = 0;
let timerId = null;
let startTime = 0;

async function postGame(payload) {
    const response = await fetch(ctx + "/api/game", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });
    let data = {};
    try {
        data = await response.json();
    } catch (err) {
        data = {};
    }
    if (!response.ok) {
        throw new Error(data.message || "Request failed");
    }
    return data;
}

// UC-8 Chơi game
// 8.1.6 Đặt bộ đếm thời gian về 0 và bắt đầu đếm khi người chơi mở ô đầu tiên
function startTimer() {
    startTime = Date.now();
    timerId = setInterval(() => {
        const elapsed = Math.floor((Date.now() - startTime) / 1000);
        timerEl.textContent = String(elapsed);
    }, 1000);
}

function stopTimer() {
    if (timerId) {
        clearInterval(timerId);
        timerId = null;
    }
}

// UC-8 Chơi game
// 8.1.6 Đặt bộ đếm thời gian về 0 (khi bắt đầu ván mới)
function resetTimer() {
    stopTimer();
    timerEl.textContent = "0";
}

function setOverlay(show, title) {
    if (!overlayEl) {

        return;
    }
    if (show) {
        overlayTitle.textContent = title;
        overlayEl.classList.remove("hidden");
    } else {
        overlayEl.classList.add("hidden");
    }
}

function updateMineCount() {
    const totalMines = boardState ? boardState.mineCount : 0;
    mineCountEl.textContent = String(totalMines - flaggedCount);
}

function updateCustomVisibility() {
    const isCustom = difficultySelect.value === "custom";
    customFields.style.display = isCustom ? "flex" : "none";
}

// UC-8 Chơi game
// 8.2.2 Hệ thống nhận độ khó được chọn
// 8.2.3 Thiết lập số hàng
// 8.2.4 Thiết lập số cột
// 8.2.5 Thiết lập số lượng mìn
function getConfig() {
    const difficulty = difficultySelect.value;
    if (difficulty === "custom") {
        return {
            difficulty,
            rows: parseInt(customRows.value, 10),
            cols: parseInt(customCols.value, 10),
            mines: parseInt(customMines.value, 10)
        };
    }
    return { difficulty };
}

// UC-8 Chơi game
// 8.1.3 Khởi tạo bàn chơi (UI: tạo phần tử ô trên giao diện)
// 8.1.7 Hiển thị bàn chơi mới
function renderBoard(board) {
    boardEl.innerHTML = "";
    boardEl.style.setProperty("--cols", board.cols);

    for (let r = 0; r < board.rows; r++) {
        for (let c = 0; c < board.cols; c++) {
            const cell = document.createElement("div");
            cell.className = "cell";
            cell.dataset.row = String(r);
            cell.dataset.col = String(c);
            cell.addEventListener("click", () => handleReveal(r, c));
            cell.addEventListener("contextmenu", (event) => {
                event.preventDefault();
                handleFlag(r, c);
            });
            boardEl.appendChild(cell);
        }
    }
}

const FLAG_SVG = `<svg class="cell-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
  <path d="M5 21V4" stroke="#8b5a36" stroke-width="2.2" stroke-linecap="round"/>
  <path d="M5 4L17 9L5 14Z" fill="#a45b4a" stroke="#8b5a36" stroke-width="0.5"/>
</svg>`;

const MINE_SVG = `<svg class="cell-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
  <circle cx="12" cy="12" r="6" fill="#4b3a2a"/>
  <line x1="12" y1="3" x2="12" y2="6" stroke="#4b3a2a" stroke-width="2" stroke-linecap="round"/>
  <line x1="12" y1="18" x2="12" y2="21" stroke="#4b3a2a" stroke-width="2" stroke-linecap="round"/>
  <line x1="3" y1="12" x2="6" y2="12" stroke="#4b3a2a" stroke-width="2" stroke-linecap="round"/>
  <line x1="18" y1="12" x2="21" y2="12" stroke="#4b3a2a" stroke-width="2" stroke-linecap="round"/>
  <line x1="5.6" y1="5.6" x2="7.8" y2="7.8" stroke="#4b3a2a" stroke-width="2" stroke-linecap="round"/>
  <line x1="16.2" y1="16.2" x2="18.4" y2="18.4" stroke="#4b3a2a" stroke-width="2" stroke-linecap="round"/>
  <line x1="5.6" y1="18.4" x2="7.8" y2="16.2" stroke="#4b3a2a" stroke-width="2" stroke-linecap="round"/>
  <line x1="16.2" y1="7.8" x2="18.4" y2="5.6" stroke="#4b3a2a" stroke-width="2" stroke-linecap="round"/>
  <circle cx="9.5" cy="10" r="2" fill="#7c6a57"/>
</svg>`;

function updateCellUI(row, col, cellData) {
    const cellEl = boardEl.querySelector(`[data-row="${row}"][data-col="${col}"]`);
    if (!cellEl) {
        return;
    }
    cellEl.classList.toggle("revealed", cellData.isRevealed);
    cellEl.classList.toggle("flagged", cellData.isFlagged);
    cellEl.classList.toggle("mine", cellData.isMine);
    if (cellData.isRevealed && cellData.adjacentMines > 0 && !cellData.isMine) {
        cellEl.textContent = String(cellData.adjacentMines);
    } else if (cellData.isFlagged) {
        cellEl.innerHTML = FLAG_SVG;
    } else if (cellData.isMine && cellData.isRevealed) {
        cellEl.innerHTML = MINE_SVG;
    } else {
        cellEl.textContent = "";
    }
}

function applyRevealedCells(revealedCells) {
    revealedCells.forEach((cellUpdate) => {
        const { row, col } = cellUpdate;
        const cellData = boardState.cells[row][col];
        cellData.isRevealed = cellUpdate.isRevealed;
        cellData.isFlagged = cellUpdate.isFlagged;
        cellData.isMine = cellUpdate.isMine;
        cellData.adjacentMines = cellUpdate.adjacentMines;
        updateCellUI(row, col, cellData);
    });
}

function applyFullBoard(board) {
    for (let r = 0; r < board.rows; r++) {
        for (let c = 0; c < board.cols; c++) {
            boardState.cells[r][c] = board.cells[r][c];
            updateCellUI(r, c, boardState.cells[r][c]);
        }
    }
}

// UC-8 Chơi game
// 8.3.1 Người chơi nhấn chuột trái vào ô
// 8.3.2 Hệ thống kiểm tra trạng thái ô
// 8.3.3 Nếu ô đã mở thì bỏ qua
// 8.3.4 Nếu ô có cờ thì bỏ qua
// 8.3.5 Mở ô được chọn (gửi yêu cầu reveal tới server)
// 8.3.6 Kiểm tra ô có chứa mìn (server trả về status)
// 8.3.7 Nếu chứa mìn thì kết thúc trận
// 8.3.8 Hiển thị toàn bộ mìn
// 8.3.9 Nếu không chứa mìn thì tính số mìn lân cận (server tính)
// 8.3.10 Nếu số mìn lân cận bằng 0 thì mở đệ quy các ô kề (server flood reveal)
// 8.3.11 Cập nhật giao diện
// 8.3.12 Kiểm tra điều kiện chiến thắng
async function handleReveal(row, col) {
    if (gameOver) {
        return;
    }
    const shouldStart = !started;
    if (shouldStart) {
        started = true;
        startTimer();
        showAlert("Timer started — good luck!", "info", { duration: 2000 });
    }
    try {
        const data = await postGame({ action: "reveal", row, col });
        applyRevealedCells(data.revealedCells || []);

        if (data.status === "WIN" || data.status === "LOSE") {
            gameOver = true;
            stopTimer();
            if (data.board) {
                applyFullBoard(data.board);
            }
            const isWin = data.status === "WIN";
            if (isWin) {
                playSound("victory");
            } else {
                playSound("boom");
                setTimeout(() => playSound("lose"), 400);
            }
            setOverlay(true, isWin ? "You Win" : "Game Over");
            showAlert(
                isWin ? "Congratulations! You cleared the board!" : "Boom! You hit a mine.",
                isWin ? "success" : "error",
                { duration: 5000 }
            );
            const saveData = await saveScore();
            if (saveData && typeof saveData.eloDelta === "number") {
                const delta = saveData.eloDelta;
                const after = typeof saveData.eloAfter === "number" ? ` (Elo ${saveData.eloAfter})` : "";
                if (delta === 0) {
                    showAlert(`Elo unchanged${after}`, "info", { duration: 4200 });
                } else {
                    const sign = delta > 0 ? "+" : "";
                    const tone = delta > 0 ? "success" : "error";
                    showAlert(`Elo ${sign}${delta}${after}`, tone, { duration: 4200 });
                }
            }
        } else {
            playSound("click");
        }
    } catch (err) {
        if (shouldStart) {
            started = false;
            stopTimer();
        }
        showAlert(err.message || "Could not reveal cell", "error");
    }
}

// UC-8 Chơi game
// 8.4.1 Người chơi nhấn chuột phải vào ô
// 8.4.2 Hệ thống kiểm tra trạng thái ô
// 8.4.3 Nếu ô chưa mở thì cho phép cắm cờ
// 8.4.4 Đánh dấu cờ lên ô (gửi yêu cầu flag tới server)
// 8.4.5 Cập nhật số lượng cờ còn lại
// 8.4.6 Nếu ô đã có cờ thì gỡ cờ
// 8.4.7 Cập nhật giao diện
async function handleFlag(row, col) {
    if (gameOver || !started) {
        return;
    }
    if (!boardState) {
        return;
    }
    try {
        const prevFlagged = boardState.cells[row][col].isFlagged;
        const data = await postGame({ action: "flag", row, col });
        const update = data.cell;
        if (!update) {
            return;
        }
        boardState.cells[row][col].isFlagged = update.isFlagged;
        boardState.cells[row][col].isRevealed = update.isRevealed;
        updateCellUI(row, col, boardState.cells[row][col]);

        if (!prevFlagged && update.isFlagged) {
            flaggedCount += 1;
            playSound("flag");
            showAlert("Flag placed", "info", { duration: 1500 });
        } else if (prevFlagged && !update.isFlagged) {
            flaggedCount -= 1;
            playSound("flag");
            showAlert("Flag removed", "info", { duration: 1500 });
        }
        updateMineCount();
    } catch (err) {
        showAlert(err.message || "Could not toggle flag", "error");
    }
}

// UC-8 Chơi game
// 8.5.1 Người chơi chiến thắng (hoặc thua) → hệ thống có thể lưu điểm khi có kết quả hoàn tất
// 8.5.2 Hệ thống tính điểm (server side: ScoreService)
// 8.5.3 Thu thập thông tin người chơi (uid, displayName được giữ trong session)
// 8.5.4 Tạo bản ghi điểm số (server side)
// 8.5.5 Lưu điểm vào file hoặc cơ sở dữ liệu (DAO lưu vào Firestore)
// 8.5.6 Cập nhật bảng xếp hạng (Leaderboard sẽ đọc từ DB khi cần)
// 8.5.7 Hiển thị kết quả (hiển thị Elo change trên UI)
async function saveScore() {
    try {
        const timeSec = parseInt(timerEl.textContent, 10);
        return await postGame({ action: "save", timeSec });
    } catch (err) {
        showAlert(err.message || "Could not save score", "error", { duration: 5200 });
    }
    return null;
}

async function newGame() {
    resetTimer();
    started = false;
    gameOver = false;
    flaggedCount = 0;
    setOverlay(false, "");
    updateCustomVisibility();
    try {
        const config = getConfig();
        const data = await postGame({ action: "new", ...config });
        boardState = data.board;
        renderBoard(boardState);
        updateMineCount();
        const label = (config.difficulty || "easy").charAt(0).toUpperCase() + (config.difficulty || "easy").slice(1);
        showAlert(`New ${label} game started — click any cell to begin!`, "success", { duration: 2500 });
    } catch (err) {
        showAlert(err.message || "Could not start a new game", "error");
    }
}

if (difficultySelect) {
    difficultySelect.addEventListener("change", () => {
        updateCustomVisibility();
        newGame();
    });
}

if (newGameBtn) {
    newGameBtn.addEventListener("click", newGame);
}

if (overlayNew) {
    overlayNew.addEventListener("click", newGame);
}

updateCustomVisibility();
newGame();
