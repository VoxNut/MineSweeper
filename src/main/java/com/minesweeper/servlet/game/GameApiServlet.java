package com.minesweeper.servlet.game;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.minesweeper.model.Board;
import com.minesweeper.model.Cell;
import com.minesweeper.service.GameService;
import com.minesweeper.service.ScoreService;

public class GameApiServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(GameApiServlet.class.getName());
    private final Gson gson = new Gson();
    private final GameService gameService = new GameService();
    private final ScoreService scoreService = new ScoreService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        JsonObject body = gson.fromJson(request.getReader(), JsonObject.class);
        if (body == null || !body.has("action")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(response, "Missing action");
            return;
        }

        String action = body.get("action").getAsString();
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeError(response, "No session");
            return;
        }

        try {
            switch (action) {
                case "new" -> handleNew(body, session, response);
                case "reveal" -> handleReveal(body, session, response);
                case "flag" -> handleFlag(body, session, response);
                case "save" -> handleSave(body, session, response);
                default -> {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    writeError(response, "Unknown action");
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Game API error", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeError(response, "Server error");
        }
    }

    private void handleNew(JsonObject body, HttpSession session, HttpServletResponse response) throws IOException {
        String difficulty = getString(body, "difficulty", "easy");
        ServletContext context = session.getServletContext();
        int rows = resolveRows(context, difficulty, body);
        int cols = resolveCols(context, difficulty, body);
        int mines = resolveMines(context, difficulty, body);

        Board board = gameService.createEmptyBoard(rows, cols, mines);
        session.setAttribute("boardJson", gson.toJson(board));
        session.setAttribute("difficulty", difficulty);
        session.setAttribute("boardRows", rows);
        session.setAttribute("boardCols", cols);
        session.setAttribute("mineCount", mines);
        session.removeAttribute("lastResult");

        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "OK");
        payload.put("board", toClientBoard(board, false));
        gson.toJson(payload, response.getWriter());
    }

    private void handleReveal(JsonObject body, HttpSession session, HttpServletResponse response) throws IOException {
        Board board = getBoardFromSession(session);
        if (board == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(response, "No board in session");
            return;
        }
        int row = getInt(body, "row", -1);
        int col = getInt(body, "col", -1);
        if (row < 0 || col < 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(response, "Invalid cell");
            return;
        }

        if (!board.isMinesPlaced()) {
            int rows = (int) session.getAttribute("boardRows");
            int cols = (int) session.getAttribute("boardCols");
            int mines = (int) session.getAttribute("mineCount");
            board = gameService.generateBoard(rows, cols, mines, row, col);
        }

        GameService.RevealResult result = gameService.revealCell(board, row, col);
        if (!"CONTINUE".equals(result.getStatus())) {
            session.setAttribute("lastResult", result.getStatus().toLowerCase());
        }
        session.setAttribute("boardJson", gson.toJson(board));

        Map<String, Object> payload = new HashMap<>();
        payload.put("status", result.getStatus());
        payload.put("revealedCells", result.getRevealedCells());
        if (!"CONTINUE".equals(result.getStatus())) {
            payload.put("board", toClientBoard(board, true));
        }
        gson.toJson(payload, response.getWriter());
    }

    private void handleFlag(JsonObject body, HttpSession session, HttpServletResponse response) throws IOException {
        Board board = getBoardFromSession(session);
        if (board == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(response, "No board in session");
            return;
        }
        if (!board.isMinesPlaced()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(response, "Cannot flag before first reveal");
            return;
        }
        int row = getInt(body, "row", -1);
        int col = getInt(body, "col", -1);
        if (row < 0 || col < 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(response, "Invalid cell");
            return;
        }

        Cell cell = board.getCell(row, col);
        if (!cell.isRevealed()) {
            cell.setFlagged(!cell.isFlagged());
        }
        session.setAttribute("boardJson", gson.toJson(board));

        GameService.CellUpdate update = new GameService.CellUpdate(
                row,
                col,
                cell.getAdjacentMines(),
                false,
                cell.isFlagged(),
                cell.isRevealed()
        );
        Map<String, Object> payload = new HashMap<>();
        payload.put("cell", update);
        gson.toJson(payload, response.getWriter());
    }

    private void handleSave(JsonObject body, HttpSession session, HttpServletResponse response) throws Exception {
        String uid = (String) session.getAttribute("uid");
        if (uid == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeError(response, "Not logged in");
            return;
        }
        Board board = getBoardFromSession(session);
        if (board == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(response, "No board in session");
            return;
        }
        String result = (String) session.getAttribute("lastResult");
        if (result == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(response, "No finished game to save");
            return;
        }
        int timeSec = getInt(body, "timeSec", 0);
        String difficulty = (String) session.getAttribute("difficulty");
        String displayName = (String) session.getAttribute("displayName");

        ScoreService.SaveResult saveResult = scoreService.saveScore(uid, displayName, board, timeSec, result, difficulty);
        Map<String, Object> payload = new HashMap<>();
        payload.put("success", true);
        payload.put("eloBefore", saveResult.getEloBefore());
        payload.put("eloAfter", saveResult.getEloAfter());
        payload.put("eloDelta", saveResult.getEloDelta());
        gson.toJson(payload, response.getWriter());
    }

    private Board getBoardFromSession(HttpSession session) {
        Object json = session.getAttribute("boardJson");
        if (json == null) {
            return null;
        }
        return gson.fromJson(json.toString(), Board.class);
    }

    private Board toClientBoard(Board board, boolean revealMines) {
        Board client = new Board(board.getRows(), board.getCols(), board.getMineCount());
        client.setMinesPlaced(board.isMinesPlaced());
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Cell source = board.getCell(r, c);
                Cell target = client.getCell(r, c);
                target.setAdjacentMines(source.getAdjacentMines());
                target.setFlagged(source.isFlagged());
                boolean mineVisible = revealMines && source.isMine();
                target.setMine(mineVisible);
                target.setRevealed(source.isRevealed() || mineVisible);
            }
        }
        return client;
    }

    private String getString(JsonObject body, String key, String defaultValue) {
        JsonElement el = body.get(key);
        return el != null ? el.getAsString() : defaultValue;
    }

    private int getInt(JsonObject body, String key, int defaultValue) {
        JsonElement el = body.get(key);
        return el != null ? el.getAsInt() : defaultValue;
    }

    private int resolveRows(ServletContext context, String difficulty, JsonObject body) {
        Integer customRows = body.has("rows") ? body.get("rows").getAsInt() : null;
        try {
            return gameService.resolveRows(context, difficulty, customRows);
        } catch (Exception ex) {
            return fallbackRows(difficulty, body);
        }
    }

    private int resolveCols(ServletContext context, String difficulty, JsonObject body) {
        Integer customCols = body.has("cols") ? body.get("cols").getAsInt() : null;
        try {
            return gameService.resolveCols(context, difficulty, customCols);
        } catch (Exception ex) {
            return fallbackCols(difficulty, body);
        }
    }

    private int resolveMines(ServletContext context, String difficulty, JsonObject body) {
        Integer customMines = body.has("mines") ? body.get("mines").getAsInt() : null;
        try {
            return gameService.resolveMines(context, difficulty, customMines);
        } catch (Exception ex) {
            return fallbackMines(difficulty, body);
        }
    }

    private int fallbackRows(String difficulty, JsonObject body) {
        if ("medium".equalsIgnoreCase(difficulty)) {
            return 16;
        }
        if ("hard".equalsIgnoreCase(difficulty)) {
            return 16;
        }
        if ("custom".equalsIgnoreCase(difficulty)) {
            return getInt(body, "rows", 9);
        }
        return 9;
    }

    private int fallbackCols(String difficulty, JsonObject body) {
        if ("medium".equalsIgnoreCase(difficulty)) {
            return 16;
        }
        if ("hard".equalsIgnoreCase(difficulty)) {
            return 30;
        }
        if ("custom".equalsIgnoreCase(difficulty)) {
            return getInt(body, "cols", 9);
        }
        return 9;
    }

    private int fallbackMines(String difficulty, JsonObject body) {
        if ("medium".equalsIgnoreCase(difficulty)) {
            return 40;
        }
        if ("hard".equalsIgnoreCase(difficulty)) {
            return 99;
        }
        if ("custom".equalsIgnoreCase(difficulty)) {
            return getInt(body, "mines", 10);
        }
        return 10;
    }

    private void writeError(HttpServletResponse response, String message) throws IOException {
        Map<String, Object> payload = new HashMap<>();
        payload.put("success", false);
        payload.put("message", message);
        gson.toJson(payload, response.getWriter());
    }
}
